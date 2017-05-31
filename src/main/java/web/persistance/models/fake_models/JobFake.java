package web.persistance.models.fake_models;


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
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<JobFake> depends_on;
    private String ends;

    public JobFake(){
        this.depends_on = new ArrayList<>();
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

    public List<JobFake> getDepends_on() {
        return depends_on;
    }

    public void setDepends_on(List<JobFake> depends_on) {
        this.depends_on = depends_on;
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
}
