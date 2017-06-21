package web.dtos;


import web.LogType;

public class Log implements Comparable<Log>{
    private int id;
    private String createdAt;
    private CardReduced card;
    private Board board;
    private LogType type;
    private String description;

    public Log(){}

    public Log(int id, String createdAt, CardReduced card, Board board, LogType type, String description) {
        this.id = id;
        this.createdAt = createdAt;
        this.card = card;
        this.board = board;
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

    public CardReduced getCard() {
        return card;
    }

    public void setCard(CardReduced card) {
        this.card = card;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
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

    //DESC ORDER
    /*public int compareTo(Log other) {
        return Integer.compare(other.getId(),this.id);
    }*/

    public int compareTo(Log other) {
        return Integer.compare(this.id,other.getId());
    }
}
