package web.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

/**
 * Created by Miquel on 13/02/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Board {
    private String id;
    private String name;
    private String url;

    public Board(){
    }

    public Board(String id, String name, String url){
        this.id = id;
        this.name = name;
        this.url = url;
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
}
