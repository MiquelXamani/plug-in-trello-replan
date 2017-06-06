package web.persistance.fake_models;


import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class JobFake {
    @Id
    private int id;
    private String starts;
    @ManyToOne
    @JoinColumn(name = "resourceId")
    @NotFound(action = NotFoundAction.IGNORE)
    private ResourceFake resource;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "featureId")
    @NotFound(action = NotFoundAction.IGNORE)
    private FeatureFake feature;
    private String ends;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "planId")
    @NotFound(action = NotFoundAction.IGNORE)
    private PlanFake plan;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="tbl_precedence",
            joinColumns=@JoinColumn(name="previousJobId"),
            inverseJoinColumns=@JoinColumn(name="nextJobId")
    )
    @NotFound(action = NotFoundAction.IGNORE)
    private List<JobFake> previous;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="tbl_precedence",
            joinColumns=@JoinColumn(name="nextJobId"),
            inverseJoinColumns=@JoinColumn(name="previousJobId")
    )
    @NotFound(action = NotFoundAction.IGNORE)
    private List<JobFake> next;

    public JobFake(){
        previous = new ArrayList<>();
        next = new ArrayList<>();
    }

    public JobFake(int id, String starts, String ends, PlanFake plan) {
        this.id = id;
        this.starts = starts;
        this.ends = ends;
        this.plan = plan;
        previous = new ArrayList<>();
        next = new ArrayList<>();
    }

    public ResourceFake getResource() {
        return resource;
    }

    public void setResource(ResourceFake resource) {
        this.resource = resource;
    }

    public FeatureFake getFeature() {
        return feature;
    }

    public void setFeature(FeatureFake feature) {
        this.feature = feature;
    }

    public String getStarts() {
        return starts;
    }

    public void setStarts(String starts) {
        this.starts = starts;
    }

    public String getEnds() {
        return ends;
    }

    public void setEnds(String ends) {
        this.ends = ends;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PlanFake getPlan() {
        return plan;
    }

    public void setPlan(PlanFake plan) {
        this.plan = plan;
    }

    public List<JobFake> getPrevious() {
        return previous;
    }

    public void setPrevious(List<JobFake> previous) {
        this.previous = previous;
    }

    public List<JobFake> getNext() {
        return next;
    }

    public void setNext(List<JobFake> next) {
        this.next = next;
    }

    public void addPrevious(JobFake jobFake){
        previous.add(jobFake);
    }

    public void addNext(JobFake jobFake){
        next.add(jobFake);
    }
}
