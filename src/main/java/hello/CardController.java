package hello;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

//Controller que gestiona l'endpoint /hello-world

@Controller
@RequestMapping("/card")
public class CardController {

    @RequestMapping(method=RequestMethod.GET)
    public @ResponseBody Card obtainCard() {
        RestTemplate restTemplate = new RestTemplate();
        Card card = restTemplate.getForObject("https://api.trello.com/1/cards/T8MhLdxN?fields=name,idList,idBoard,idMembers&key=504327a0a1868e4f91dae5f6c852de79&token=b9af2c827b36369367e5416dcccb657a949f4745f1b41ee7f70d2fe91f78165e", Card.class);
        return card;
    }

    @RequestMapping(method= RequestMethod.POST)
    public ResponseEntity<Card> createCard(@RequestBody Card card) {

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.trello.com/1/cards/?key={key}&token={token}";
        String key = "504327a0a1868e4f91dae5f6c852de79";
        String token = "b9af2c827b36369367e5416dcccb657a949f4745f1b41ee7f70d2fe91f78165e";
        Map<String,String> vars = new HashMap<>();
        vars.put("key",key);
        vars.put("token",token);

        //es necessita la id d'una llista que s'obté d'haver fet get card.
        String list = "5870fe35c84a2f7e5d29c2f5";
        card.setIdList(list);

        try {
            card = restTemplate.postForObject(url, card, Card.class, vars);
            return new ResponseEntity<>(card, HttpStatus.CREATED);
        }
        catch (HttpClientErrorException e){
            throw e;
        }
    }
}
