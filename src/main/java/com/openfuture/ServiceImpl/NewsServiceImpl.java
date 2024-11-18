package com.openfuture.ServiceImpl;

import com.openfuture.Entity.Admin;
import com.openfuture.Entity.News;
import com.openfuture.Exception.AdminNotFoundException;
import com.openfuture.Exception.NewsNotFound;
import com.openfuture.Repository.AdminRepository;
import com.openfuture.Repository.NewsRepository;
import com.openfuture.Service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {
    private static final Logger logger = LoggerFactory.getLogger(ContactServiceImpl.class);

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public News saveNews(News news) {
        logger.info("Creating News with title: {}", news.getNewsTitle());
        try {
            logger.info("Looking for Admin with ID ");


            Admin admin = adminRepository.findById(news.getAdmin().getId()).orElse(null);

//            News saveNews=newsRepository.findByAdminId(news.getAdmin().getId()).orElseThrow(null);
            if (admin == null) {
                logger.error("Admin with Id {} does not exist",news.getAdmin().getId());
                throw new AdminNotFoundException("Admin with Id " +news.getAdmin().getId()+ " is not available");
            }

            return newsRepository.save(news);
        } catch (AdminNotFoundException e) {
            logger.error("Admin not found error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {

            logger.error("Error while creating News", e);
            throw new RuntimeException("Error saving news: " + e.getMessage(), e);
        }
    }

    @Override
    public News getNewsByNewsId(Long id) {
        logger.info("Fetching News by news id {}",id);

        try{
            return newsRepository.findById(id).orElseThrow(() -> new NewsNotFound("News not found with id {}"+id));
        }catch (NewsNotFound e){
            logger.error("Error while fetching news",e.getMessage());
            throw new RuntimeException();
        }
    }



    @Override
    public News getNewsByAdminId(Long adminId) {
        logger.info("Fetching news by adminId {}", adminId);
        return newsRepository.findByAdminId(adminId)
                .orElseThrow(() -> new NewsNotFound("News not found for adminId: " + adminId));
    }

    @Override
    public List<News> getAllNews() {
        try{
            logger.info("Getting all news");
            return newsRepository.findAll();
        } catch(NewsNotFound e){
            logger.error("Error while fetching all news");
            throw new RuntimeException("Error while getting all news");

        }
    }

    @Override
    public News updateNewsByNewsId(Long newsId, News news) {
        logger.info("Updating news by adminId {}", newsId);
        News existingNews = newsRepository.findById(newsId)
                .orElseThrow(() -> new NewsNotFound("News not found for adminId: " + newsId));

        // Update the news content
        existingNews.setNewsTitle(news.getNewsTitle());
        existingNews.setNewsContent(news.getNewsContent());

        return newsRepository.save(existingNews);
    }

    @Override
    public void deleteNewsByNewsId(Long newsId) {
        logger.info("Deleting news by adminId {}", newsId);
        News existingNews = newsRepository.findById(newsId)
                .orElseThrow(() -> new NewsNotFound("News not found for adminId: " + newsId));

        newsRepository.delete(existingNews);
    }
}