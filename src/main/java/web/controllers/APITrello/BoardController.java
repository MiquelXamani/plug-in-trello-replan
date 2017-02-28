package web.controllers.APITrello;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import web.models.Board;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Miquel on 13/02/2017.
 */

@Controller
@RequestMapping("/board")
public class BoardController {
    @RequestMapping(method= RequestMethod.POST)
    public ResponseEntity<Board> createBoard(@RequestBody Board board) {

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.trello.com/1/boards/?key={key}&token={token}";
        String key = "504327a0a1868e4f91dae5f6c852de79";
        String token = "b9af2c827b36369367e5416dcccb657a949f4745f1b41ee7f70d2fe91f78165e";
        Map<String,String> vars = new HashMap<>();
        vars.put("key",key);
        vars.put("token",token);

        try {
            board = restTemplate.postForObject(url, board, Board.class, vars);
            return new ResponseEntity<>(board, HttpStatus.CREATED);
        }
        catch (HttpClientErrorException e){
            throw e;
        }
    }
}
