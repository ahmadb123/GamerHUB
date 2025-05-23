package com.models.NewsModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Genres {
    private int id; // Genre ID
    private int games_count; // Number of games in this genre
    private String name; // Genre name
    private String slug;
    private String image_background;
    
    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_background() {
        return image_background;
    }

    public String getSlug(){
        return slug;
    }

    public void setSlug(String slug){
        this.slug = slug;
    }

    public void setImage_background(String image_background) {
        this.image_background = image_background;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGames_count() {
        return games_count;
    }

    public void setGames_count(int games_count) {
        this.games_count = games_count;
    }

}
