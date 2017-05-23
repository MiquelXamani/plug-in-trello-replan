package web.domain;


import web.LogType;

public class Log implements Comparable<Log>{
    private int id;
    private String createdAt;
    private String boardId;
    private String boardName;
    private String cardId;
    private String cardName;
    private boolean accepted;
    private boolean rejected;
    private LogType type;
    private String description;

    public Log(){}

    public Log(int id, String createdAt, String boardId, String boardName, String cardId, String cardName, boolean accepted, boolean rejected, LogType type, String description) {
        this.id = id;
        this.createdAt = createdAt;
        this.boardId = boardId;
        this.boardName = boardName;
        this.cardId = cardId;
        this.cardName = cardName;
        this.accepted = accepted;
        this.rejected = rejected;
        this.type = type;
        this.description = description;
    }

    public Log(String createdAt, String boardId, String boardName, String cardId, String cardName, LogType type, String description) {
        this.createdAt = createdAt;
        this.boardId = boardId;
        this.boardName = boardName;
        this.cardId = cardId;
        this.cardName = cardName;
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

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean read) {
        this.accepted = read;
    }

    public LogType getType() {
        return type;
    }

    public void setType(LogType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public boolean isRejected() {
        return rejected;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    //DESC ORDER
    /*public int compareTo(Log other) {
        return Integer.compare(other.getId(),this.id);
    }*/

    public int compareTo(Log other) {
        return Integer.compare(this.id,other.getId());
    }
}
