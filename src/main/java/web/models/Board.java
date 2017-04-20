package web.models;

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
@Entity
public class Board {
    @Id
    private String id;
    private String name;
    private String url;
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private Set<Label> labels;

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

    public Set<Label> getLabels() {
        return labels;
    }

    public void setLabels(Set<Label> labels) {
        this.labels = labels;
    }
}
