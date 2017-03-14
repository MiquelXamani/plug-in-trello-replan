package web.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Job {
    private String ends;
    private List<Resource> resources;
    private Feature feature;
    private List<Job> depends_on;

    public Job(){
        this.resources = new ArrayList<>();
        this.depends_on = new ArrayList<>();
    }

    public String getEnds() {
        return ends;
    }

    public void setEnds(String ends) {
        this.ends = ends;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
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
