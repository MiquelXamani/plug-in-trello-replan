package web.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miquel on 01/03/2017.
 */
public class Team {
    private String id;
    private String displayName;
    private List<Member> members;

    public Team(){
        members = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }
}
