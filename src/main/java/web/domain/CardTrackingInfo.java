package web.domain;


import web.LogType;

public class CardTrackingInfo {
    private int logId;
    private String memberUsername;
    private String createdAt;
    private String listName;
    private LogType type;

    public CardTrackingInfo(){}

    public CardTrackingInfo(int logId, String memberUsername, String createdAt, String listName, LogType type) {
        this.logId = logId;
        this.memberUsername = memberUsername;
        this.createdAt = createdAt;
        this.listName = listName;
        this.type = type;
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

    public LogType getType() {
        return type;
    }

    public void setType(LogType type) {
        this.type = type;
    }
}
