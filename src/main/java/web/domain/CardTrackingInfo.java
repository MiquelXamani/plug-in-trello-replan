package web.domain;


public class CardTrackingInfo {
    private int logId;
    private String memberUsername;
    private String createdAt;
    private String listName;

    public CardTrackingInfo(){}

    public CardTrackingInfo(int logId, String memberUsername, String createdAt, String listName) {
        this.logId = logId;
        this.memberUsername = memberUsername;
        this.createdAt = createdAt;
        this.listName = listName;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getMemberUsername() {
        return memberUsername;
    }

    public void setMemberUsername(String memberUsername) {
        this.memberUsername = memberUsername;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}
