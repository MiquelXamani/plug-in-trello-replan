package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.LogType;
import web.domain.Action;
import web.domain.Card;
import web.domain.Label;
import web.domain.aux_classes.IdNameObject;
import web.domain.aux_classes.WebhookCardTrelloResponse;
import web.persistence_controllers.PersistenceController;
import web.services.TrelloService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public void createLog(String boardId, String boardName, String cardId, String cardName, String memberUsername, LogType logType){
        System.out.println("Create log function params:");
        System.out.println("boardId: " + boardId + " boardName: " + boardName + " cardId: " + cardId + " cardName: " + cardName + " member: " + memberUsername);
        persistenceController.saveLog(boardId,boardName,cardId,cardName,memberUsername,logType);
    }

    @RequestMapping(value = "/cards", method= RequestMethod.POST)
    public ResponseEntity<String> cardModified(@RequestParam(value = "username") String webUserTrelloUsername, @RequestBody WebhookCardTrelloResponse response) throws ParseException {
        System.out.println("Trello notified me!!");
        Action action = response.getAction();
        String boardId = action.getData().getBoard().getId();
        String boardName = action.getData().getBoard().getName();
        IdNameObject listAfter = action.getData().getListAfter();
        IdNameObject listBefore = action.getData().getListBefore();
        if (action.getType().equals("updateCard") &&  listAfter != null){
            String newListId = listAfter.getId();
            String oldListId = listBefore.getId();
            System.out.println(newListId + " " + listAfter.getName());

            String actionCreator = action.getMemberCreator().getUsername();
            if(actionCreator.equals(webUserTrelloUsername)){
                actionCreator = "";
            }

            //Get id lists
            String doneListId = persistenceController.getListId(boardId,"Done");
            String onHoldListId = persistenceController.getListId(boardId,"On-hold");
            String readyListId =  persistenceController.getListId(boardId,"Ready");
            String inProgressListId = persistenceController.getListId(boardId,"In Progress");

            Card card = response.getModel();
            String cardId = card.getId();
            String cardName = card.getName();

            if(newListId.equals(doneListId)){
                System.out.println("CARD MOVED TO DONE LIST");

                //get usertoken
                TrelloService trelloService = new TrelloService();
                String userToken = persistenceController.getBoardUser(boardId).getTrelloToken();

                //borrar label verda de la card (si en té)
                System.out.println("Board id: " + boardId);
                String greenLabelId = persistenceController.getLabelId(boardId,"green");
                if(cardHasLabel(greenLabelId,card.getIdLabels())){
                    System.out.println("Card id: "+ cardId);
                    trelloService.removeLabel(cardId,greenLabelId,userToken);
                }

                //borrar label vermella de la card (si en té)
                String redLabelId = persistenceController.getLabelId(boardId,"red");
                if(cardHasLabel(redLabelId,card.getIdLabels())){
                    trelloService.removeLabel(cardId,redLabelId,userToken);
                }

                //afegir nova label a la card
                String purpleLabelId = persistenceController.getLabelId(boardId,"purple");
                if(!cardHasLabel(purpleLabelId,card.getIdLabels())) {
                    trelloService.addLabel(card.getId(), purpleLabelId, userToken);
                }

                //moure les cards que depenien de la card moguda d'on-hold a ready
                System.out.println("+++++++depending cards part+++++++++");
                List<Card> dependingCards = trelloService.getDependingCards(boardId,cardId,cardName,userToken);

                for (Card c: dependingCards) {
                    System.out.println(c.getName());
                }

                String description, dependsOnText, dependsOnCards;
                dependsOnText = "**Depends on:** ";
                int startIndex, textSize, endIndex, count;
                textSize = dependsOnText.length();
                List<String> dependsOnList;

                Card[] cardsDone = trelloService.getListCards(doneListId,userToken);
                boolean found;
                String yellowLabelId = persistenceController.getLabelId(boardId,"yellow");
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

                    if(dependsOnList.size() == 1){
                        //remove yellow label
                        System.out.println("Yellow label removed! (Only 1 dependency)");
                        trelloService.removeLabel(c.getId(),yellowLabelId,userToken);
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
                        System.out.println("Count: " + count + " dependsOnList size: "+dependsOnList.size());
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
                List<Card> nextCards = trelloService.getNextCards(boardId,card.getIdMembers(),onHoldListId,inProgressListId,readyListId,userToken);
                System.out.println("cards moved from On-Hold to ready: ");

                List<String> nextCardsIds = new ArrayList<>();
                for (int i = 0; i < nextCards.size(); i++) {
                    System.out.println(nextCards.get(i).getName());
                    nextCardsIds.add(nextCards.get(i).getId());
                }



                trelloService.moveCardsAndAddLabel(nextCardsIds,readyListId,greenLabelId,userToken);

                //create log
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Date dueDate = dateFormat.parse(card.getDue());
                Date currentDate = new Date();
                LogType logType;
                if(currentDate.after(dueDate)){
                    logType = LogType.FINISHED_LATE;
                    System.out.println("LATER");
                }
                else{
                    logType = LogType.FINISHED_EARLIER;
                    System.out.println("EARLIER");
                }
                createLog(boardId,boardName,cardId,cardName,action.getMemberCreator().getUsername(),logType);

            }
            else if (newListId.equals(readyListId)){
                System.out.println("CARD MOVED TO READY LIST");
                if(actionCreator.equals("")){
                    actionCreator = "Project Leader";
                }
                createLog(boardId,boardName,cardId,cardName,actionCreator,LogType.MOVED_TO_READY);
            }
            else if (newListId.equals(inProgressListId)){
                System.out.println("CARD MOVED TO IN PROGRESS LIST");
                if(!actionCreator.equals("")) {
                    persistenceController.saveLog(boardId, boardName, cardId, cardName, actionCreator, LogType.MOVED_TO_IN_PROGRESS);
                }
            }
            else{
                System.out.println("ANOTHER CARD MOVEMENT");
            }
        }
        else{
            System.out.println("NOT A CARD MOVEMENT");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/cards", method= RequestMethod.GET)
    public ResponseEntity<String> checkG(@RequestParam(value = "username") String username){
        System.out.println("Trello checked and username is " + username);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
