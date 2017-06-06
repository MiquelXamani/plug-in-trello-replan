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
}
