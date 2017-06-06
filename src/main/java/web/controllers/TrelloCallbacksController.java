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
import web.domain.operation_classes.CardDependency;
import web.persistence_controllers.PersistenceController;
import web.services.TrelloService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/trello-callbacks")
public class TrelloCallbacksController {
    private PersistenceController persistenceController;
    private CardDependency cardDependency;

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


    private Card getNextCard(List<Card>cardsAssigned, String readyListId, String inProgressListId,String onHoldListId) throws ParseException {
        System.out.println("*** GetNextCard Function ***");
        boolean workingInOtherCard = false;
        String idList;
        Card cardAssigned;
        Card nextCard = null;
        //Es separa per no fer crides innecessàries a l'API de Trello que farien anar més lent
        for (int j = 0; !workingInOtherCard && j < cardsAssigned.size(); j++) {
            cardAssigned = cardsAssigned.get(j);
            idList = cardAssigned.getIdList();
            if(idList.equals(inProgressListId) || idList.equals(readyListId)){
                workingInOtherCard = true;
                System.out.println("Working in another card: " + cardAssigned.getName());
            }
        }
        if(!workingInOtherCard){
            System.out.println("NOT working in another card");
            String description, date, earliestDate = "";
            String startDateText = "**Start date:** ";
            int textLength = startDateText.length();
            String datePattern = "yyyy/MM/dd HH:mm:ss";
            int dateLength = datePattern.length();
            int startDateTextIndex, startDateValueIndex, startDateValueIndexFinal;
            SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
            Date d;
            Date earliestD = new Date();
            for(int k = 0; k < cardsAssigned.size(); k++){
                cardAssigned = cardsAssigned.get(k);
                idList = cardAssigned.getIdList();
                if(idList.equals(onHoldListId)){
                    //Susbtituir per crida a cardAssigned.getStartDate()
                    description = cardAssigned.getDesc();
                    startDateTextIndex = description.indexOf(startDateText);
                    if (startDateTextIndex > -1) {
                        startDateValueIndex = startDateTextIndex + textLength;
                        startDateValueIndexFinal = startDateValueIndex + dateLength;
                        date = description.substring(startDateValueIndex, startDateValueIndexFinal);
                    //Final. d = cardAssigned.getStartDate();
                        d = dateFormat.parse(date);
                        if (earliestDate.equals("") || earliestD.after(d)) {
                            earliestDate = date;
                            earliestD = d;
                            nextCard = cardAssigned;
                        }
                    }
                }
            }
        }
        if(nextCard != null){
            System.out.println("Next card:" + nextCard.getName());
        }
        else{
            System.out.println("Next card null");
        }
        return nextCard;
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

                //Part de les cards dependents on s'elimina la label groga a aquelles cards que ja no depenen de cap altra
                //i es troba la next card per aquells membres que estaven assignats a una de les cards dependents de la card moguda
                //a Done però que no estan assignats a aquesta carta
                //Exemple d'aquest cas: M1 assignat a T1, M2 assignat a T2, T2 depèn de T1
                System.out.println("+++++++depending cards part+++++++++");
                List<Card> dependingCards = trelloService.getDependingCards(boardId,cardId,cardName,userToken);

                //Only for testing purposes
                String print = "";
                for (Card c: dependingCards) {
                    print += c.getName()+" ";
                }
                System.out.println("Depending cards of "+card.getName()+": "+print);

                Card[] cardsDone = trelloService.getListCards(doneListId,userToken);

                //fer funció per utilitzar la part d'extreure les dependències de la descripció i borrar les labels grogues
                String yellowLabelId = persistenceController.getLabelId(boardId,"yellow");
                Map<String,Card> nextCardsMap = new HashMap<>();
                Card nextCard;
                List<Card> cardsAssigned;
                cardDependency = new CardDependency();
                for (Card c: dependingCards) {
                    //Aquesta card dependent encara depèn d'alguna card més que no estigui finalitzada?
                    boolean depends = cardDependency.stillDependsOnAnotherCard(c,cardsDone);
                    if(!depends){
                        //remove yellow label
                        trelloService.removeLabel(c.getId(),yellowLabelId,userToken);

                        List<String> idMembersDependingCards = c.getIdMembers();
                        String idMember;
                        for(int i = 0; i < idMembersDependingCards.size(); i++){
                            idMember = idMembersDependingCards.get(i);
                            if(!card.isAssigned(idMember)){
                                System.out.println("Member not assigned to card moved");
                                cardsAssigned = trelloService.getMemberCards(idMember,boardId,userToken);
                                nextCard = getNextCard(cardsAssigned,readyListId,inProgressListId,onHoldListId);
                                nextCardsMap.put(nextCard.getId(),nextCard);
                            }
                        }

                    }
                }


                System.out.println("---------next card part, move to ready and add green label-------");
                //posar green label a les següents card i moure-les a Ready, l'actual de cada membre
                System.out.println(card.getIdMembers().size());
                //a part dels membres assignats a la card moguda, també s'han d'afegir els membres assignats a les cards que se'ls hi ha
                //eliminat la label groga les nextcard dels quals s'han trobat abans.
                boolean depends2;
                List<Card> cardsAssignedNotDepending;
                Card cardAssigned;
                for(String idM:card.getIdMembers()){
                    cardsAssignedNotDepending = new ArrayList<>();
                    cardsAssigned = trelloService.getMemberCards(idM,boardId,userToken);
                    for(int i = 0; i < cardsAssigned.size(); i++){
                        cardAssigned = cardsAssigned.get(i);
                        //Si la card no està a On-hold, no fa falta continuar
                        if(cardAssigned.getIdList().equals(onHoldListId)) {
                            System.out.println("Card assigned: " + cardAssigned.getName());
                            depends2 = cardDependency.stillDependsOnAnotherCard(cardAssigned, cardsDone);
                            if (!depends2) {
                                System.out.println("Depends is FALSE");
                                cardsAssignedNotDepending.add(cardAssigned);
                            } else {
                                System.out.println("Depends is TRUE");
                            }
                        }
                    }
                    //Només les que no depenen d'una card sense finalitzar poden ser next card
                    nextCard = getNextCard(cardsAssignedNotDepending,readyListId,inProgressListId,onHoldListId);
                    if(nextCard != null) {
                        nextCardsMap.put(nextCard.getId(), nextCard);
                    }
                    else{
                        System.out.println("Next card is null");
                    }
                }

                System.out.println("cards moved from On-Hold to ready: ");

                List<String> nextCardsIds = new ArrayList<>(nextCardsMap.keySet());
                List<Card> nextCards = new ArrayList<>(nextCardsMap.values());
                for (int i = 0; i < nextCards.size(); i++) {
                    System.out.println(nextCards.get(i).getName());
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
