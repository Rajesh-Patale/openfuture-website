package com.openfuture.Controller;

import com.openfuture.Entity.Admin;
import com.openfuture.Entity.News;
import com.openfuture.Exception.NewsNotFound;
import com.openfuture.Service.NewsService;
import com.openfuture.ServiceImpl.ContactServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/News")
@CrossOrigin("*")
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(ContactServiceImpl.class);

    @Autowired
    private NewsService newsService;

    @PostMapping("/SaveNews")
    public ResponseEntity<News> SaveNews(@RequestBody News news){
        logger.info("Received request to save news");
        try{


            News saveNews=newsService.saveNews(news);
            return new ResponseEntity<>(saveNews, HttpStatus.CREATED);
        }catch(Exception e){
            logger.error("Error While Creating News {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getNewsById/{id}")
    public ResponseEntity<News> getNewsById(@PathVariable Long id) {
        try {
            logger.info("Request for getting News with id {}", id);
            News news = newsService.getNewsByNewsId(id);
            return ResponseEntity.ok(news);
        } catch (NewsNotFound e) {
            logger.error("News not found with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/allnews")
    public ResponseEntity<List<News>> getAllnews(){
        try{
            logger.info("Fetching all news");
            List<News> AllNews=newsService.getAllNews();
            return new  ResponseEntity<>(AllNews,HttpStatus.OK);
        }catch(NewsNotFound e){
            logger.info("error while fetching all news",e.getMessage());
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/getNewsByAdminId/{adminId}")
    public ResponseEntity<News> getNewsByAdminId(@PathVariable Long adminId) {
        try {
            logger.info("Request for getting News with adminId {}", adminId);
            News news = newsService.getNewsByAdminId(adminId);
            return ResponseEntity.ok(news);
        } catch (NewsNotFound e) {
            logger.error("News not found for adminId {}: {}", adminId, e.getMessage(), e);
            return ResponseEntity.status(404).build();
        }
    }



    @PutMapping("/update/{adminId}")
    public ResponseEntity<News> updateNews(@PathVariable Long adminId,
                                           @RequestParam String title,
                                           @RequestParam String content) {
        try {
            News updatedNews = newsService.updateNewsByAdminId(adminId, title, content);
            return new ResponseEntity<>(updatedNews, HttpStatus.OK);
        } catch (NewsNotFound e) {
            logger.error("Error while updating news for adminId {}: {}", adminId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error while updating news {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("/delete/{adminId}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long adminId) {
        try {
            newsService.deleteNewsByAdminId(adminId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (NewsNotFound e) {
            logger.error("Error while deleting news for adminId {}: {}", adminId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error while deleting news {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
