package web.services;

import org.springframework.web.client.RestTemplate;
import web.models.*;

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
    private String url;
    Map<String,String> vars;
    Map<String,String> input;

    public TrelloService(){}

    public String getTrelloUserUsername(String userToken){
        url = "https://api.trello.com/1/members/me?key={key}&token={token}";
        vars = new HashMap<>();
        vars.put("key",key);
        vars.put("token",userToken);
        User u = restTemplate.getForObject(url,User.class,vars);
        return u.getUsername();
    }

    public Team[] getTrelloTeams(String username, String userToken){
        url = "https://api.trello.com/1/members/{username}/organizations?key={key}&token={token}";
        vars = new HashMap<>();
        vars.put("username",username);
        vars.put("key",key);
        vars.put("token",userToken);
        Team[] teams = restTemplate.getForObject(url,Team[].class,vars);
        return teams;
    }

    public TeamWithMembers getTrelloTeamMembers(String idTeam, String userToken){
        url = "https://api.trello.com/1/organizations/{idTeam}?members=all&member_fields=username,fullName&fields=displayName&key={key}&token={token}";
        vars = new HashMap<>();
        vars.put("key",key);
        vars.put("token",userToken);
        vars.put("idTeam",idTeam);
        TeamWithMembers teamWithMembers = restTemplate.getForObject(url,TeamWithMembers.class,vars);
        return teamWithMembers;
    }

    public Board createBoard(String planName, String boardName, String idTeam, String userToken){
        url = "https://api.trello.com/1/boards/?key={key}&token={token}";
        vars = new HashMap<>();
        vars.put("key",key);
        vars.put("token",userToken);
        input = new HashMap<>();
        input.put("name",boardName);
        input.put("defaultLists","false");
        input.put("desc",planName + " planification. Board created by Replan plug-in for Trello");
        input.put("idOrganization",idTeam);
        input.put("prefs_permissionLevel","org");
        Board board = restTemplate.postForObject(url,input,Board.class,vars);
        return board;
    }

    public List<ListTrello> createLists(String idBoard,String userToken){
        url = "https://api.trello.com/1/lists/?key={key}&token={token}";
        vars = new HashMap<>();
        vars.put("key",key);
        vars.put("token",userToken);
        input = new HashMap<>();
        input.put("idBoard", idBoard);
        String[] listNames = {"Notifications","On-hold","Ready","In Progress","Done"};
        ArrayList<ListTrello> lists = new ArrayList<>();
        for(int i = listNames.length - 1; i >= 0; i--){
            input.put("name", listNames[i]);
            ListTrello l = restTemplate.postForObject(url,input,ListTrello.class,vars);
            lists.add(l);
        }

        return lists;
    }

    public void addMemberToBoard(String boardId, String trelloUserId, String userToken){
        url = "https://api.trello.com/1/boards/{boardId}/members/{memberId}?type=normal&key={key}&token={token}";
        vars = new HashMap<>();
        vars.put("boardId",boardId);
        vars.put("memberId",trelloUserId);
        vars.put("key",key);
        vars.put("token",userToken);
        restTemplate.put(url,null,vars);
    }

    public List<Card> createCards(List<Card> cards,String userToken){
        url = "https://api.trello.com/1/cards/?key={key}&token={token}";
        vars = new HashMap<>();
        vars.put("key",key);
        vars.put("token",userToken);
        List <Card> cardsCreated = new ArrayList<>();
        Card c;
        System.out.println("Cards size: " + cards.size());
        for (Card card: cards) {
            String print = "Name: " + card.getName() + " Desc: " + card.getDesc() + " Due: " + card.getDue() + " idList: " + card.getIdList() + " idMembers: [";
            for(int i = 0; i < card.getIdMembers().size(); i++){
                print += card.getIdMembers().get(i) + " ,";
            }
            print += "]";
            System.out.println(print);
            c = restTemplate.postForObject(url,card,Card.class,vars);
            cardsCreated.add(c);
        }
        return cardsCreated;
    }

    public Label[] getLabels(String boardId,String userToken){
        url = "https://api.trello.com/1/boards/{boardId}/labels?key={key}&token={token}";
        vars = new HashMap<>();
        vars.put("key",key);
        vars.put("token",userToken);
        vars.put("boardId",boardId);
        Label[] labels = restTemplate.getForObject(url,Label[].class,vars);
        return labels;
    }

}
