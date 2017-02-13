package hello;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

//Controller que gestiona l'endpoint /hello-world

@Controller
@RequestMapping("/card")
public class CardController {
    RestTemplate restTemplate = new RestTemplate();
    Card card = restTemplate.getForObject("https://api.trello.com/1/cards/T8MhLdxN?fields=name,idList,idBoard,idMembers&key=504327a0a1868e4f91dae5f6c852de79&token=b9af2c827b36369367e5416dcccb657a949f4745f1b41ee7f70d2fe91f78165e", Card.class);
    @RequestMapping(method=RequestMethod.GET)
    public @ResponseBody Card obtainCard() {
        return card;
    }
}
