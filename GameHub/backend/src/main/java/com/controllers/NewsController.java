// this is the controller for the news api
package com.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;   
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.models.NewsModel.Genres;
import com.models.NewsModel.News;
import com.models.NewsModel.NewsResults;
import com.services.NewsService;                           
import com.utility.CurrentDate;
import com.utility.JWT;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private JWT jwt;
    @GetMapping("/recent-news")
    public ResponseEntity<?> getRecentNews(
        @RequestParam(value = "start_date", required = false) LocalDate startDate,
        @RequestParam(value = "end_date", required = false) LocalDate endDate,
        @RequestParam(value = "platform", required = false) String platform
    ) {
        try {
            // Use provided dates or default to the past month
            if (startDate == null) {
                startDate = CurrentDate.getDateTwoMonthsAgo();
            }
            if (endDate == null) {
                endDate = CurrentDate.getCurrentDate();
            }

            // Default platform IDs if not provided
            if (platform == null || platform.isEmpty()) {
                platform = "187,186, 2"; // PlayStation 5 and Xbox Series S/X
            }

            // Format dates
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedStartDate = startDate.format(formatter);
            String formattedEndDate = endDate.format(formatter);

       
            List<News> newsList = newsService.getNews(
                formattedStartDate,
                formattedEndDate,
                platform,
                null,           // No genre filter for recent news
                "-relevance",   // Example ordering for recent news
                10              // Top 10 results
            );

            // Return the list of news as JSON
            return ResponseEntity.ok(newsList);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                .status(500)
                .body("Failed to fetch news: " + e.getMessage());
        }
    }

    @GetMapping("get-news-by-order-selection")
    public ResponseEntity<?> renderNewsByOrderSelection(
        @RequestParam(value = "start_date", required = false) LocalDate startDate,
        @RequestParam(value = "end_date", required = false) LocalDate endDate,
        @RequestParam(value = "platform", required = false) String platform,
        @RequestParam(value = "order", required = true) String userOrderSelection){
        try{
            if(userOrderSelection.equals("-released")){
                if(startDate == null){
                    startDate = LocalDate.of(1900,1,1); //far past
                }
                if(endDate == null){
                    endDate = LocalDate.of(2027,1,1); //far future
                }
            }else{
                if (startDate == null) {
                    startDate = CurrentDate.getDateTwoMonthsAgo();
                }
                if (endDate == null) {
                    endDate = CurrentDate.getCurrentDate();
                }
            }

            if(platform == null || platform.isEmpty()){
                platform = "187,186, 2"; // PlayStation 5 and Xbox Series S/X
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedStartDate = startDate.format(formatter);
            String formattedEndDate = endDate.format(formatter);
            List<News> newsList = newsService.getNews(
                formattedStartDate,
                formattedEndDate,
                platform,
                null,
                userOrderSelection,
                10
            );
            return ResponseEntity.ok(newsList);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity
                .status(500)
                .body("Failed to fetch news: " + e.getMessage());
        }
    }

    @GetMapping("all-news")
    public ResponseEntity<?> getAllNews(
        @RequestParam (value = "platform", required = false) String platform,
        @RequestParam (value = "genre", required = false) String genre)
        {
        try{
            if(platform == null || platform.isEmpty()){
                platform = "187,186, 2"; // PlayStation 5 and Xbox Series S/X
            }
            if(genre == null || genre.isEmpty()){
                genre = "action,adventure,strategy";
            }
            LocalDate startDate = null;
            LocalDate endDate = null;
            if (startDate == null) {
                startDate = CurrentDate.getDateTwoMonthsAgo();
            }
            if (endDate == null) {
                endDate = CurrentDate.getCurrentDate();
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedStartDate = startDate.format(formatter);
            String formattedEndDate = endDate.format(formatter);
            List<News> newsList = newsService.getNews(
                formattedStartDate,           // No start date
                formattedEndDate,           // No end date
                platform,
                genre,
                "-relevance",      // Ordering by rating as an example
                40              // Return more results
            );
            return ResponseEntity.ok(newsList);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity
                .status(500)
                .body("Failed to fetch news: " + e.getMessage());
        }
    }
    @GetMapping("/genres")
    public ResponseEntity<?> getGenres(){
        try{
            List<Genres> genres = newsService.getAllGenres();
            return ResponseEntity.ok(genres);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity
                .status(500)
                .body("Failed to fetch genres: " + e.getMessage());
        }
    }

    @GetMapping("/search-game")
    public ResponseEntity<?> getGame(@RequestParam String gameName){
        try{
            List<News> newsList = newsService.searchForGame(gameName);
            return ResponseEntity.ok(newsList);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity
                .status(500)
                .body("Failed to fetch game: " + e.getMessage());
        }
    }

    // this api is for ** SAVING GAME **
    @GetMapping("/get-game-by-id")
    public ResponseEntity<?> getGameById(@RequestHeader("Authorization") String authHeader){
        try{
            if(authHeader == null || authHeader.isEmpty()){
                return ResponseEntity.badRequest().body("Invalid token");
            }
            String token = authHeader.substring(7);
            Long userId = jwt.extractUserId(token);
            if(userId == null){
                return ResponseEntity.badRequest().body("Invalid token");
            }
            List<NewsResults> gameDetails = newsService.getSavedGameDetails(userId);
            return ResponseEntity.ok(gameDetails);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity
                .status(500)
                .body("Failed to fetch game: " + e.getMessage());
        }
    }

    // api for looking up a game by its id - 
    @GetMapping("/search-game-by-id/{gameId}")
    public ResponseEntity<?> getGameById(@PathVariable long gameId){
        try{
            NewsResults gameDetails = newsService.getGameById(gameId);
            return ResponseEntity.ok(gameDetails);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity
                .status(500)
                .body("Failed to fetch game: " + e.getMessage());
        }
    }
}