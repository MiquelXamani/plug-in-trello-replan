package web.models;

import javax.persistence.*;

@Entity
public class ResourceMember {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long idResourceMember;
    @Column(unique = true)
    private String resourceId;
    private String resourceName;
    @Column(unique = true)
    private String trelloUserId;
    @Column(unique = true)
    private String trelloUsername;
    private String trelloFullName;
    private String userId; //usuari de la web

    protected ResourceMember(){}

    public ResourceMember(String userId, String resourceId, String resourceName, String trelloUserId, String trelloUsername, String trelloFullName){
        this.userId = userId;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.trelloUserId = trelloUserId;
        this.trelloUsername = trelloUsername;
        this.trelloFullName = trelloFullName;
    }
}
