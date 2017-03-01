package web.controllers.APITrello;

import org.springframework.web.client.RestTemplate;
import web.models.Team;
import web.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Miquel on 28/02/2017.
 */
public class TrelloService {
    private RestTemplate restTemplate = new RestTemplate();
    private String key = "504327a0a1868e4f91dae5f6c852de79";

    public TrelloService(){}

    public String getTrelloUserUsername(String userToken){
        String url = "https://api.trello.com/1/members/me?key={key}&token={token}";
        Map<String,String> vars = new HashMap<>();
        vars.put("key",key);
        vars.put("token",userToken);
        User u = restTemplate.getForObject(url,User.class,vars);
        return u.getUsername();
    }

    public Team[] getTrelloTeam(String username, String userToken){
        String url = "https://api.trello.com/1/members/{username}/organizations?key={key}&token={token}";
        Map<String,String> vars = new HashMap<>();
        vars.put("username",username);
        vars.put("key",key);
        vars.put("token",userToken);
        Team[] teams = restTemplate.getForObject(url,Team[].class,vars);
        return teams;
    }

}
