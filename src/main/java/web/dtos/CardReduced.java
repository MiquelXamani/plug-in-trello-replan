package web.dtos;

public class CardReduced {
    private String id;
    private String name;
    private String boardId;
    private boolean accepted;
    private boolean rejected;
    private boolean alive;

    public CardReduced(String id, String name, String boardId, boolean accepted, boolean rejected, boolean alive) {
        this.id = id;
        this.name = name;
        this.boardId = boardId;
        this.accepted = accepted;
        this.rejected = rejected;
        this.alive = alive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isRejected() {
        return rejected;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
