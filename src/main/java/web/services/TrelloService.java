package web.services;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import web.domain.*;
import web.domain.aux_classes.SearchCardResponse;
import web.persistance.models.User;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

    public Board createBoard(String boardName, String idTeam, String userToken){
        url = "https://api.trello.com/1/boards/?key={key}&token={token}";
        vars = new HashMap<>();
        vars.put("key",key);
        vars.put("token",userToken);
        input = new HashMap<>();
        input.put("name",boardName);
        input.put("defaultLists","false");
        //posar el nom del projecte i de la release
        //input.put("desc",planName + " planification. Board created by Replan plug-in for Trello");
        input.put("idOrganization",idTeam);
        input.put("prefs_permissionLevel","org");
        Board board = restTemplate.postForObject(url,input,Board.class,vars);
        return board;
    }

    public ListTrello[] createLists(String idBoard,String userToken){
        url = "https://api.trello.com/1/lists/?key={key}&token={token}";
        vars = new HashMap<>();
        vars.put("key",key);
        vars.put("token",userToken);
        input = new HashMap<>();
        input.put("idBoard", idBoard);
        String[] listNames = {"Notifications","On-hold","Ready","In Progress","Done"};
        ListTrello lists[] = new ListTrello[5];
        for(int i = listNames.length - 1; i >= 0; i--){
            input.put("name", listNames[i]);
            ListTrello l = restTemplate.postForObject(url,input,ListTrello.class,vars);
            lists[i] = l;
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

    public List<Webhook> createWebhooks(List<Card> cards, String userToken){
        url = "https://api.trello.com/1/tokens/{token}/webhooks/?key={key}";
        vars = new HashMap<>();
        vars.put("key",key);
        vars.put("token",userToken);
        List<Webhook> createdWebhooks = new ArrayList<>();
        Webhook webhook;
        String description = "Card webhook";
        String callbackUrl = "https://glacial-anchorage-60164.herokuapp.com/trello-callbacks/cards";
        for(Card card : cards) {
            webhook = restTemplate.postForObject(url,new Webhook(description,callbackUrl,card.getId()),Webhook.class,vars);
            createdWebhooks.add(webhook);
        }
        return createdWebhooks;
    }

    public void removeLabel(String cardId, String labelId, String userToken) throws RestClientException{
        url = "https://api.trello.com/1/cards/{cardId}/idLabels/{labelId}?key={key}&token={token}";
        vars = new HashMap<>();
        vars.put("key",key);
        vars.put("token",userToken);
        vars.put("cardId",cardId);
        vars.put("labelId",labelId);
        restTemplate.delete(url,vars);
    }

    public String[] addLabel(String cardId, String labelId, String userToken){
        url = "https://api.trello.com/1/cards/{cardId}/idLabels?key={key}&token={token}";
        vars = new HashMap<>();
        vars.put("key",key);
        vars.put("token",userToken);
        vars.put("cardId",cardId);
        vars.put("labelId",labelId);
        input = new HashMap<>();
        input.put("value",labelId);
        return restTemplate.postForObject(url,input,String[].class,vars);
    }

    public List<Card> getDependingCards(String boardId, String cardId, String cardName, String userToken){
        url = "https://api.trello.com/1/search?query=board%3A{boardId}%20%26%20description%3A{depends}&modelTypes=cards&key={key}&token={token}";
        vars = new HashMap<>();
        vars.put("key",key);
        vars.put("token",userToken);
        vars.put("boardId",boardId);
        String dependsText = "depends%20on%3A%20";
        dependsText += encodeURIComponent(cardName);
        System.out.println(dependsText);
        vars.put("depends",dependsText);
        url = "https://api.trello.com/1/search?query=board%3A"+boardId+"%20%26%20description%3A"+dependsText+"&modelTypes=cards&key="+key+"&token="+userToken;
        System.out.println(url);
        SearchCardResponse searchCardResponse = restTemplate.getForObject(url,SearchCardResponse.class);
        List <Card> cardsFound = searchCardResponse.getCards();
        System.out.println("Cards found size: " + cardsFound.size());
        boolean found = false;
        //this call not only returns depending cards, it also returns the card moved to done list
        for(int i = 0; !found && i < cardsFound.size(); i++){
            if(cardsFound.get(i).getId().equals(cardId)){
                found = true;
                System.out.println("Card found: " + cardsFound.remove(i).getName());
                //cardsFound.remove(i);
            }
        }
        return cardsFound;
    }

    public void moveCards(List<Card> cards, String listId, String userToken){
        url = "https://api.trello.com/1/cards/{cardId}?key={key}&token={token}";
        vars = new HashMap<>();
        vars.put("key",key);
        vars.put("token",userToken);
        input = new HashMap<>();
        input.put("idList",listId);
        for (Card card : cards) {
            vars.put("cardId",card.getId());
            restTemplate.put(url,input,vars);
        }
    }

    private String encodeURIComponent(String component){
        String result;

        try
        {
            result = URLEncoder.encode(component, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e)
        {
            result = component;
        }

        return result;
    }
}
