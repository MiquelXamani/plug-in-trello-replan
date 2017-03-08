package web.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miquel on 01/03/2017.
 */
public class Team {
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
}
