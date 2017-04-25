package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import web.domain.Action;
import web.domain.aux_classes.IdNameObject;
import web.domain.aux_classes.WebhookCardTrelloResponse;
import web.persistence_controllers.PersistenceController;

import java.util.Map;

@RestController
@RequestMapping("/trello-callbacks")
public class TrelloCallbacksController {
    private PersistenceController persistenceController;

    @Autowired
    public TrelloCallbacksController(PersistenceController persistenceController){
        this.persistenceController = persistenceController;
    }

    @RequestMapping(value = "/cards", method= RequestMethod.POST)
    public ResponseEntity<String> cardModified(@RequestBody WebhookCardTrelloResponse response) {
        System.out.println("Trello notified me!!");
        Action action = response.getAction();
        IdNameObject listAfter = action.getData().getListAfter();
        if (action.getType().equals("updateCard") &&  listAfter != null){
            System.out.println(listAfter.getId() + " " + listAfter.getName());
            if(persistenceController.isReadyList(listAfter.getId())){
                System.out.println("CARD MOVED TO DONE LIST");
                //borrar label de la card
                //afegir nova label a la card
                //posar green label a les següents card
                //moure les cards que depenien de la card moguda d'on-hold a ready
            }
            else {
                System.out.println("CARD MOVED TO ANOTHER LIST");
            }
        }
        else{
            System.out.println("NOT A CARD MOVEMENT");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/cards", method= RequestMethod.GET)
    public ResponseEntity<String> checkG(){
        System.out.println("Trello checked!!");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
