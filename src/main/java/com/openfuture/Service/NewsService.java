package com.openfuture.Service;

import com.openfuture.Entity.News;
import com.openfuture.Repository.NewsRepository;

import java.util.List;

public interface NewsService{

    News  saveNews(News news);
    News getNewsByNewsId(Long id);
    List<News> getAllNews();
    News getNewsByAdminId(Long adminId);

    News updateNewsByNewsId(Long newsId, News news);

    void deleteNewsByNewsId(Long newsId);


}
