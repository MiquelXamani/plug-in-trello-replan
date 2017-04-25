package web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/trello-callbacks")
public class TrelloCallbacksController {

    @RequestMapping(value = "/cards", method= RequestMethod.POST)
    public ResponseEntity<String> cardModified(@RequestBody String payload) {
        System.out.println("Trello notified me!!");
        System.out.println(payload);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
