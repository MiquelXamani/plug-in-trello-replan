package web.persistance.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class JobPersist {
    @Id
    private int id;
    private int featureId;
    @ManyToOne
    @JoinColumn(name = "idCard")
    private CardPersist card;

    public JobPersist(){}

    public JobPersist(int id, CardPersist card, int featureId) {
        this.id = id;
        this.card = card;
        this.featureId = featureId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
