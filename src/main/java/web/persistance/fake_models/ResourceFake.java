package web.persistance.fake_models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ResourceFake {
    @Id
    private int id;
    private String name;
    private String description;
    @OneToMany(mappedBy = "resource")
    private List<JobFake> jobs;

    public ResourceFake(){
        jobs = new ArrayList<>();
    }

    public ResourceFake(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        jobs = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<JobFake> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobFake> jobs) {
        this.jobs = jobs;
    }

    public void addJob(JobFake jobFake){
        jobs.add(jobFake);
    }
}
