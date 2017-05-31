package web.persistance.models.fake_models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class ResourceFake {
    @Id
    private int id;
    private String name;
    private String description;
    @OneToMany(mappedBy = "resource")
    private List<JobFake> jobs;

    public ResourceFake(){}

    public ResourceFake(int id, String name, String description){
        this.id = id;
        this.name = name;
        this.description = description;
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

    public List<JobFake> getJob() {
        return jobs;
    }

    public void setJob(List<JobFake> jobs) {
        this.jobs = jobs;
    }
}
