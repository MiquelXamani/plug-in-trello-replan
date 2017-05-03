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
    private boolean read;
    @ManyToOne
    @JoinColumn(name = "boardId")
    private BoardPersist board;
    private String cardId;
    private String cardName;
    private String memberUsername;
    private String type;
    private String description;

    public LogPersist(){}

    public LogPersist(String createdAt,boolean read, BoardPersist boardPersist,String cardId,String cardName,String memberUsername,String type){
        this.createdAt = createdAt;
        this.read = read;
        this.board = boardPersist;
        this.cardId = cardId;
        this.cardName = cardName;
        this.memberUsername = memberUsername;
        this.type = type;
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

    public boolean getRead(){
        return read;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public BoardPersist getBoard() {
        return board;
    }

    public void setBoard(BoardPersist board) {
        this.board = board;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
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
