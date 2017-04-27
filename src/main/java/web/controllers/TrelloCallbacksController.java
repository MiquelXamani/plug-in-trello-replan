package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import web.domain.Action;
import web.domain.Card;
import web.domain.Label;
import web.domain.aux_classes.IdNameObject;
import web.domain.aux_classes.WebhookCardTrelloResponse;
import web.persistence_controllers.PersistenceController;
import web.services.TrelloService;

import java.util.List;
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
                TrelloService trelloService = new TrelloService();
                //canviar usertoken per un de veritat
                String userToken = "b9af2c827b36369367e5416dcccb657a949f4745f1b41ee7f70d2fe91f78165e";

                //borrar label de la card
                System.out.println("Board id: " + action.getData().getBoard().getId());
                String greenLabelId = persistenceController.getGreenLabelId(action.getData().getBoard().getId());
                System.out.println("Green label id: " + greenLabelId);
                Card card = response.getModel();
                List<String> idLabels = card.getIdLabels();
                boolean found = false;
                for(int i = 0; !found && i < idLabels.size(); i++){
                    System.out.println("id label" + i + ": " + idLabels.get(i));
                    if(idLabels.get(i).equals(greenLabelId)){
                        System.out.println("GREEN LABEL FOUND!");
                        found = true;
                        trelloService.removeLabel(card.getId(),greenLabelId,userToken);
                    }
                }



                //afegir nova label a la card
                //moure les cards que depenien de la card moguda d'on-hold a ready
                //posar green label a les següents card

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
