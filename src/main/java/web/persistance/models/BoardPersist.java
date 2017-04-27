package web.persistance.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import web.domain.Board;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Set;

/**
 * Created by Miquel on 13/02/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class BoardPersist {
    @Id
    private String id;
    private String name;
    private String url;
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<LabelPersist> labels;
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<ListTrelloPersist> lists;

    public BoardPersist(){
    }

    public BoardPersist(String id, String name, String url){
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

    public List<LabelPersist> getLabels() {
        return labels;
    }

    public void setLabels(List<LabelPersist> labels) {
        this.labels = labels;
    }

    public List<ListTrelloPersist> getLists() {
        return lists;
    }

    public void setLists(List<ListTrelloPersist> lists) {
        this.lists = lists;
    }
}