package web.models;


public class PlanBoardDTO extends PlanDTO {
    private String boardName;

    public PlanBoardDTO(){}

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }
}
