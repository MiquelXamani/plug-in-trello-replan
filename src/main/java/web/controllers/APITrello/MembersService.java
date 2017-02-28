package web.controllers.APITrello;

import org.springframework.web.client.RestTemplate;
import web.models.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Miquel on 28/02/2017.
 */
public class MembersService {
    private RestTemplate restTemplate = new RestTemplate();
    private String url = "https://api.trello.com/1/members/me?key={key}&token={token}";
    private String key = "504327a0a1868e4f91dae5f6c852de79";

    public MembersService(){}

    public String getTrelloUserUsername(String userToken){
        Map<String,String> vars = new HashMap<>();
        vars.put("key",key);
        vars.put("token",userToken);
        User u = restTemplate.getForObject(url,User.class,vars);
        return u.getUsername();
    }
}
