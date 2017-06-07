package web.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Job {
    private int id;
    private String starts;
    private Resource resource;
    private Feature feature;
    private List<JobReduced> depends_on;
    private String ends;

    public Job(){
        this.depends_on = new ArrayList<>();
    }

    public Job(int id, String starts, Resource resource, Feature feature, List<JobReduced> depends_on, String ends) {
        this.id = id;
        this.starts = starts;
        this.resource = resource;
        this.feature = feature;
        this.depends_on = depends_on;
        this.ends = ends;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public List<JobReduced> getDepends_on() {
        return depends_on;
    }

    public void setDepends_on(List<JobReduced> depends_on) {
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
