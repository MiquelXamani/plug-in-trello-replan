package web.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Feature {
    private int id;
    private String name;
    private String description;
    private double effort;
    private String deadline;

    public Feature(){}

    public Feature(int id, String name, double effort){
        this.id = id;
        this.name = name;
        this.effort = effort;
    }

    public Feature(int id, String name, String description, double effort, String deadline) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.effort = effort;
        this.deadline = deadline;
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
