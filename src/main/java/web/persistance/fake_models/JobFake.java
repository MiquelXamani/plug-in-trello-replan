package web.persistance.fake_models;


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
    private ResourceFake resource;
    @ManyToOne
    @JoinColumn(name = "featureId")
    private FeatureFake feature;
    private String ends;
    @ManyToOne
    @JoinColumn(name = "planId")
    private PlanFake plan;

    @ManyToMany
    @JoinTable(name="tbl_precedence",
            joinColumns=@JoinColumn(name="previousJobId"),
            inverseJoinColumns=@JoinColumn(name="nextJobId")
    )
    private List<JobFake> previous;

    @ManyToMany
    @JoinTable(name="tbl_precedence",
            joinColumns=@JoinColumn(name="nextJobId"),
            inverseJoinColumns=@JoinColumn(name="previousJobId")
    )
    private List<JobFake> next;

    public JobFake(){

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
}
