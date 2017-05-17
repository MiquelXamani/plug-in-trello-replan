package web.persistance.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import web.domain.Board;

import javax.persistence.*;
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
    @ManyToOne
    @JoinColumn(name = "userId")
    private UserPersist user;
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<LogPersist> logs;
    @ManyToOne
    @JoinColumn(name = "endpointId")
    private Endpoint endpoint;
    private int projectId;
    private int releaseId;

    public BoardPersist(){
    }

    public BoardPersist(String id, String name, String url, int projectId, int releaseId){
        this.id = id;
        this.name = name;
        this.url = url;
        this.projectId = projectId;
        this.releaseId = releaseId;
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

    public UserPersist getUser() {
        return user;
    }

    public void setUser(UserPersist user) {
        this.user = user;
    }

    public List<LogPersist> getLogs() {
        return logs;
    }

    public void setLogs(List<LogPersist> logs) {
        this.logs = logs;
    }

    public void addLog(LogPersist log){
        this.logs.add(log);
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(int releaseId) {
        this.releaseId = releaseId;
    }
}
