package web.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Job {
    private String ends;
    private List<ResourceDTO> resources;
    private List<Feature> features;
    private List<Job> depends_on;

    public Job(){
        this.resources = new ArrayList<>();
        this.features = new ArrayList<>();
        this.depends_on = new ArrayList<>();
    }

    public String getEnds() {
        return ends;
    }

    public void setEnds(String ends) {
        this.ends = ends;
    }

    public List<ResourceDTO> getResources() {
        return resources;
    }

    public void setResources(List<ResourceDTO> resources) {
        this.resources = resources;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public List<Job> getDepends_on() {
        return depends_on;
    }

    public void setDepends_on(List<Job> depends_on) {
        this.depends_on = depends_on;
    }
}
