package web.persistance.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class JobPersist {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private int jobId;
    private int featureId;
    private double featureEffort;
    private String featureName;
    @ManyToOne
    @JoinColumn(name = "cardId")
    private CardPersist card;

    public JobPersist(){}

    public JobPersist(int jobId, CardPersist card, int featureId, double featureEffort, String featureName) {
        this.jobId = jobId;
        this.card = card;
        this.featureId = featureId;
        this.featureEffort = featureEffort;
        this.featureName = featureName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public CardPersist getCard() {
        return card;
    }

    public void setCard(CardPersist card) {
        this.card = card;
    }

    public int getFeatureId() {
        return featureId;
    }

    public void setFeatureId(int featureId) {
        this.featureId = featureId;
    }

    public double getFeatureEffort() {
        return featureEffort;
    }

    public void setFeatureEffort(double featureEffort) {
        this.featureEffort = featureEffort;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }
}
