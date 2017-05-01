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

import java.text.ParseException;
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

    private boolean cardHasLabel(String idLabel, List<String> idLabels){
        boolean found = false;
        for(int i = 0; !found && i < idLabels.size(); i++){
            if(idLabels.get(i).equals(idLabel)){
                found = true;
            }
        }
        return found;
    }

    @RequestMapping(value = "/cards", method= RequestMethod.POST)
    public ResponseEntity<String> cardModified(@RequestBody WebhookCardTrelloResponse response) throws ParseException {
        System.out.println("Trello notified me!!");
        Action action = response.getAction();
        String boardId = action.getData().getBoard().getId();
        IdNameObject listAfter = action.getData().getListAfter();
        if (action.getType().equals("updateCard") &&  listAfter != null){
            System.out.println(listAfter.getId() + " " + listAfter.getName());
            if(persistenceController.isDoneList(boardId,listAfter.getId())){
                System.out.println("CARD MOVED TO DONE LIST");

                //get usertoken
                TrelloService trelloService = new TrelloService();
                String userToken = persistenceController.getBoardUser(boardId).getTrelloToken();

                //borrar label de la card
                System.out.println("Board id: " + boardId);
                String greenLabelId = persistenceController.getGreenLabelId(boardId);
                Card card = response.getModel();
                String cardId = card.getId();
                if(cardHasLabel(greenLabelId,card.getIdLabels())){
                    System.out.println("Card id: "+ cardId);
                    trelloService.removeLabel(cardId,greenLabelId,userToken);
                }


                //afegir nova label a la card
                String purpleLabelId = persistenceController.getPurpleLabelId(boardId);
                if(!cardHasLabel(purpleLabelId,card.getIdLabels())) {
                    trelloService.addLabel(card.getId(), purpleLabelId, userToken);
                }

                //moure les cards que depenien de la card moguda d'on-hold a ready
                List<Card> dependingCards = trelloService.getDependingCards(boardId,cardId,card.getName(),userToken);

                for (Card c: dependingCards) {
                    System.out.println(c.getName());
                }

                String readyListId =  persistenceController.getListId(boardId,"Ready");
                trelloService.moveCards(dependingCards,readyListId,userToken);

                System.out.println("add green label part");
                //posar green label a les següents card, l'actual de cada membre
                String doneListId = persistenceController.getDoneListId(boardId);
                System.out.println(card.getIdMembers().size());
                List<Card> nextCards = trelloService.getNextCards(boardId,card.getIdMembers(),doneListId,userToken);
                System.out.println("green labels added to: ");
                for (Card nextCard:nextCards) {
                    System.out.println(nextCard.getName());
                    if(!cardHasLabel(greenLabelId,card.getIdLabels())) {
                        trelloService.addLabel(nextCard.getId(), greenLabelId, userToken);
                    }
                }


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
