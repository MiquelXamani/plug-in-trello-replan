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
        String boardId = action.getData().getBoard().getId();
        IdNameObject listAfter = action.getData().getListAfter();
        if (action.getType().equals("updateCard") &&  listAfter != null){
            System.out.println(listAfter.getId() + " " + listAfter.getName());
            if(persistenceController.isReadyList(boardId,listAfter.getId())){
                System.out.println("CARD MOVED TO DONE LIST");

                //get usertoken
                TrelloService trelloService = new TrelloService();
                String userToken = persistenceController.getBoardUser(boardId).getTrelloToken();

                //borrar label de la card
                System.out.println("Board id: " + boardId);
                String greenLabelId = persistenceController.getGreenLabelId(boardId);
                Card card = response.getModel();
                String cardId = card.getId();
                List<String> idLabels = card.getIdLabels();
                boolean found = false;
                for(int i = 0; !found && i < idLabels.size(); i++){
                    if(idLabels.get(i).equals(greenLabelId)){
                        found = true;
                        System.out.println("Card id: "+ cardId);
                        trelloService.removeLabel(cardId,greenLabelId,userToken);
                    }
                }

                //afegir nova label a la card
                String purpleLabelId = persistenceController.getPurpleLabelId(boardId);
                trelloService.addLabel(card.getId(),purpleLabelId,userToken);

                //moure les cards que depenien de la card moguda d'on-hold a ready
                List<Card> dependingCards = trelloService.getDependingCards(boardId,cardId,card.getName(),userToken);

                for (Card c: dependingCards) {
                    System.out.println(c.getName());
                }

                String readyListId =  persistenceController.getListId(boardId,"Ready");
                trelloService.moveCards(dependingCards,readyListId,userToken);

                //posar green label a les següents card, l'actual de cada membre


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
