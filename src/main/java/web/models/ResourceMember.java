package web.models;

import javax.persistence.*;
@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"userId", "trelloUsername"})
)

@Entity
public class ResourceMember {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long idResourceMember;
    private String resourceId;
    private String resourceName;
    private String trelloUserId;
    private String trelloUsername;
    private String trelloFullName;
    private Long userId; //usuari de la web

    protected ResourceMember(){}

    public ResourceMember(Long userId, String resourceId, String resourceName, String trelloUserId, String trelloUsername, String trelloFullName){
        this.userId = userId;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.trelloUserId = trelloUserId;
        this.trelloUsername = trelloUsername;
        this.trelloFullName = trelloFullName;
    }

    public Long getIdResourceMember() {
        return idResourceMember;
    }

    public void setIdResourceMember(Long idResourceMember) {
        this.idResourceMember = idResourceMember;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
