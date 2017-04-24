package web.domain;


public class MatchingDTO2 {
    private int resourceId;
    private String resourceName;
    private String resourceDescription;
    private String trelloUserId;
    private String trelloUsername;
    private String trelloFullName;
    private String username; //username of the web user that made the petition

    public MatchingDTO2(){}

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceDescription() {
        return resourceDescription;
    }

    public void setResourceDescription(String resourceDescription) {
        this.resourceDescription = resourceDescription;
    }

    public String getTrelloUserId() {
        return trelloUserId;
    }

    public void setTrelloUserId(String trelloUserId) {
        this.trelloUserId = trelloUserId;
    }

    public String getTrelloUsername() {
        return trelloUsername;
    }

    public void setTrelloUsername(String trelloUsername) {
        this.trelloUsername = trelloUsername;
    }

    public String getTrelloFullName() {
        return trelloFullName;
    }

    public void setTrelloFullName(String trelloFullName) {
        this.trelloFullName = trelloFullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
