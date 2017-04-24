package web.domain;

/**
 * Created by Miquel on 01/03/2017.
 */
public class Team implements Comparable<Team>{
    private String id;
    private String displayName;

    public Team(){
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

    public int compareTo(Team other) {
        return this.displayName.compareTo(other.getDisplayName());
    }
}
