package web.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    public String idUser;
    public String username;
    public String password;
    public String trelloToken;
    public String trelloUsername;

    public User(){}

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
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
}
