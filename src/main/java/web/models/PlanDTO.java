package web.models;

/**
 * Created by Miquel on 14/03/2017.
 */
public class PlanDTO extends Plan {
    private String username;

    public PlanDTO(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
