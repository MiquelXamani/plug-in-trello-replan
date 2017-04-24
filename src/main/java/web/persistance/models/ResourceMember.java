package web.persistance.models;

import javax.persistence.*;
@Table(
        uniqueConstraints={
        @UniqueConstraint(columnNames={"userId", "trelloUsername"}),
        @UniqueConstraint(columnNames={"userId", "resourceId"})}
)

@Entity
public class ResourceMember {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long idResourceMember;
    private int resourceId;
    private String resourceName;
    private String resourceDescription;
    private String trelloUserId;
    private String trelloUsername;
    private String trelloFullName;
    private Long userId; //usuari de la web

    protected ResourceMember(){}

    public ResourceMember(Long userId, int resourceId, String resourceName, String resourceDescription, String trelloUserId, String trelloUsername, String trelloFullName){
        this.userId = userId;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.resourceDescription = resourceDescription;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
