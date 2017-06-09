package web.persistance.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class LogPersist {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private String createdAt;
    @ManyToOne
    @JoinColumn(name = "cardId")
    private CardPersist card;
    private String memberUsername;
    private String type;
    private String description;

    public LogPersist(){}

    public LogPersist(String createdAt,CardPersist cardPersist,String memberUsername,String type, String description){
        this.createdAt = createdAt;
        this.card = cardPersist;
        this.memberUsername = memberUsername;
        this.type = type;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public CardPersist getCard() {
        return card;
    }

    public void setCard(CardPersist card) {
        this.card = card;
    }

    public String getMemberUsername() {
        return memberUsername;
    }

    public void setMemberUsername(String memberUsername) {
        this.memberUsername = memberUsername;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
