package web.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Job {
    private String ends;
    private Resource resource;
    private Feature feature;
    private List<Job> depends_on;

    public Job(){
        this.depends_on = new ArrayList<>();
    }

    public String getEnds() {
        return ends;
    }

    public void setEnds(String ends) {
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

    public List<Job> getDepends_on() {
        return depends_on;
    }

    public void setDepends_on(List<Job> depends_on) {
        this.depends_on = depends_on;
    }
}
