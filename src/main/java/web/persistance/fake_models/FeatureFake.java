package web.persistance.fake_models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class FeatureFake {
    @Id
    private int id;
    private String name;
    private String description;
    private double effort;
    private String deadline;
    @OneToMany(mappedBy = "feature")
    private List<JobFake> jobs;

    public FeatureFake(){}

    public FeatureFake(int id, String name, String description, double effort, String deadline, List<JobFake> jobs) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.effort = effort;
        this.deadline = deadline;
        this.jobs = jobs;
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

    public double getEffort() {
        return effort;
    }

    public void setEffort(double effort) {
        this.effort = effort;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
