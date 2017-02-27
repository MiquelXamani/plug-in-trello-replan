package web.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long idUser;
    @Column(unique = true)
    private String username;
    private String password;
    private String trelloToken;
    private String trelloUsername;

    protected User(){}

    public User(String username, String password, String trelloToken, String trelloUsername){
        this.username = username;
        this.password = password;
        this.trelloToken = trelloToken;
        this.trelloUsername = trelloUsername;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTrelloToken() {
        return trelloToken;
    }

    public void setTrelloToken(String trelloToken) {
        this.trelloToken = trelloToken;
    }

    public String getTrelloUsername() {
        return trelloUsername;
    }

    public void setTrelloUsername(String trelloUsername) {
        this.trelloUsername = trelloUsername;
    }

    @Override
    public String toString() {
        return String.format(
                "User[userId=%d, username='%s', password='%s', trelloToken='%s', trelloUsername='%s']",
                idUser, username, password, trelloToken, trelloUsername);
    }
}
