package web.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Miquel on 13/02/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Board {
    String name;
    String id;
    String url;
    String shortUrl;

    public Board(){
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

}
