package web.persistance.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class CardPersist {
    @Id
    private String id;
    private String name;
    @OneToMany(mappedBy = "card")
    private List<LogPersist> logs;
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<JobPersist> jobs;

    public CardPersist(){}

    public CardPersist(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LogPersist> getLogs() {
        return logs;
    }

    public void setLogs(List<LogPersist> logs) {
        this.logs = logs;
    }

    public List<JobPersist> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobPersist> jobs) {
        this.jobs = jobs;
    }
}
