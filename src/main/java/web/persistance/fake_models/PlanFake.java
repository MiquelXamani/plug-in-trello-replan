package web.persistance.fake_models;

import web.domain.Job;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PlanFake {
    @Id
    private int id;
    private String created_at;
    @OneToMany(mappedBy = "plan")
    private List<JobFake> jobs;

    public PlanFake(){
        this.jobs = new ArrayList<>();
        jobs = new ArrayList<>();
    }

    public PlanFake(int id, String created_at) {
        this.id = id;
        this.created_at = created_at;
        jobs = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public List<JobFake> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobFake> jobs) {
        this.jobs = jobs;
    }
}
