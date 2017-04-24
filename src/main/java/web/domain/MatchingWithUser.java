package web.domain;

/**
 * Created by Miquel on 17/04/2017.
 */
public class MatchingWithUser extends Matching {
    private String username;

    public MatchingWithUser(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
