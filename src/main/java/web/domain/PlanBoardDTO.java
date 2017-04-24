package web.domain;


public class PlanBoardDTO extends PlanDTO {
    private String boardName;
    private String teamId;

    public PlanBoardDTO(){}

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }
}
