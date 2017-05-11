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
import java.util.Arrays;
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

    public void createLog(String boardId, String cardId, String cardName, String memberUsername){
        System.out.println("Create log function params:");
        System.out.println("boardId: " + boardId + " cardId: " + cardId + " cardName: " + cardName + " member: " + memberUsername);
        persistenceController.saveFinishedEarlierLog(boardId,cardId,cardName,memberUsername);
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
                System.out.println("+++++++depending cards part+++++++++");
                List<Card> dependingCards = trelloService.getDependingCards(boardId,cardId,card.getName(),userToken);

                for (Card c: dependingCards) {
                    System.out.println(c.getName());
                }

                String description, dependsOnText, dependsOnCards;
                dependsOnText = "**Depends on:** ";
                int startIndex, textSize, endIndex, count;
                textSize = dependsOnText.length();
                List<String> dependsOnList;
                String doneListId = persistenceController.getDoneListId(boardId);
                Card[] cardsDone = trelloService.getListCards(doneListId,userToken);
                boolean found;
                String yellowLabelId = persistenceController.getYellowLabelId(boardId);
                for (Card c: dependingCards) {
                    description = c.getDesc();
                    startIndex = description.indexOf(dependsOnText) + textSize;
                    endIndex = description.length();
                    dependsOnCards = description.substring(startIndex,endIndex);
                    System.out.println("substring: " + dependsOnCards);
                    dependsOnList = Arrays.asList(dependsOnCards.split(",[ ]*"));

                    System.out.println("depends list size: " + dependsOnList.size());
                    for(int i = 0; i < dependsOnList.size(); i++){
                        System.out.println(dependsOnList.get(i));
                    }

                    System.out.println("cards in done list size: " + cardsDone.length);
                    for(int j = 0; j < cardsDone.length; j++){
                        System.out.println(cardsDone[j].getName());
                    }

                    if(dependsOnList.size() < 1){
                        //remove yellow label
                        trelloService.removeLabel(c.getId(),yellowLabelId,userToken);
                        System.out.println("Yellow label removed! (Only 1 dependency)");
                    }
                    else{
                        count = 0;
                        found = false;
                        for(int i = 0; i < dependsOnList.size(); i++){
                            for(int j = 0; !found && j < cardsDone.length; j++){
                                if(cardsDone[j].getName().equals(dependsOnList.get(i))){
                                    found = true;
                                    count++;
                                }
                            }
                        }
                        if(count == dependsOnList.size()){
                            //remove yellow label
                            trelloService.removeLabel(c.getId(),yellowLabelId,userToken);
                            System.out.println("Yellow label removed! (More than 1 dependency)");
                        }
                    }
                }


                System.out.println("---------next card part, move to ready and add green label-------");
                //posar green label a les següents card i moure-les a Ready, l'actual de cada membre
                System.out.println(card.getIdMembers().size());
                String onHoldListId = persistenceController.getOnHoldListId(boardId);
                String readyListId =  persistenceController.getListId(boardId,"Ready");
                List<Card> nextCards = trelloService.getNextCards(boardId,card.getIdMembers(),onHoldListId,userToken);
                System.out.println("cards moved from On-Hold to ready: ");
                for (int i = 0; i < nextCards.size(); i++) {
                    System.out.println(nextCards.get(i).getName());
                    if(!cardHasLabel(greenLabelId,nextCards.get(i).getIdLabels())) {
                        nextCards.get(i).addLabel(greenLabelId);
                        //trelloService.addLabel(nextCard.getId(), greenLabelId, userToken);
                    }
                }
                trelloService.moveCards(nextCards,readyListId,userToken);

                //create log
                createLog(boardId,cardId,card.getName(),action.getMemberCreator().getUsername());

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
