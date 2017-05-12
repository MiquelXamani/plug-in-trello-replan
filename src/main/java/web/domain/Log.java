package web.domain;


import web.LogType;

public class Log implements Comparable<Log>{
    private int id;
    private String createdAt;
    private String boardId;
    private String boardName;
    private String cardId;
    private String cardName;
    private boolean read;
    private LogType type;
    private String description;

    public Log(){}

    public Log(int id, String createdAt, String boardId, String boardName, String cardId, String cardName, boolean read, LogType type, String description) {
        this.id = id;
        this.createdAt = createdAt;
        this.boardId = boardId;
        this.boardName = boardName;
        this.cardId = cardId;
        this.cardName = cardName;
        this.read = read;
        this.type = type;
        this.description = description;
    }

    public Log(String createdAt, String boardId, String cardId, String cardName, LogType type, String description) {
        this.createdAt = createdAt;
        this.boardId = boardId;
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

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
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

    public int compareTo(Log other) {
        return Integer.compare(other.getId(),this.id);
    }
}
