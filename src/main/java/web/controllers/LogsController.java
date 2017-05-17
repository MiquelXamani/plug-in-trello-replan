package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import web.LogType;
import web.domain.Card;
import web.domain.CardRejection;
import web.domain.Log;
import web.domain.User2;
import web.domain.aux_classes.CompleteLogOp;
import web.persistence_controllers.PersistenceController;
import web.services.TrelloService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/logs")
public class LogsController {
    private PersistenceController persistenceController;

    @Autowired
    public LogsController(PersistenceController persistenceController){
        this.persistenceController = persistenceController;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Log> getLogs(@RequestParam(value = "username") String username, @RequestParam(value = "boardId",required=false) String boardId) {
        List<Log> logs;
        //all logs
        if(boardId == null){
            logs = persistenceController.getLogs(username);
        }
        else{
           logs = persistenceController.getBoardLogs(boardId);
        }
        Collections.sort(logs);
        return logs;
    }

    @RequestMapping(value = "/reject-card",method = RequestMethod.POST)
    public Log rejectCard(@RequestBody CardRejection rejection){
        System.out.println("Reject process");
        User2 user = persistenceController.getUser(rejection.getUsername());
        String userToken = user.getTrelloToken();
        String cardId = rejection.getCardId();
        String boardId = rejection.getBoardId();
        TrelloService trelloService = new TrelloService();

        //remove green label
        String greenLabelId = persistenceController.getLabelId(boardId,"green");
        trelloService.removeLabel(cardId,greenLabelId,userToken);

        //move card to In Progress list and add red label
        String inProgressListId = persistenceController.getListId(boardId,"In Progress");
        String redLabelId = persistenceController.getLabelId(boardId,"red");
        List<String> cardsIdList = new ArrayList<>();
        cardsIdList.add(cardId);
        trelloService.moveCardsAndAddLabel(cardsIdList,inProgressListId,redLabelId,userToken);

        //add comment
        trelloService.postComment(cardId,rejection.getComment(),userToken);

        //set previous finished log as rejected
        persistenceController.setRejectedPreviousFinishedLog(cardId);

        //create log
        String boardName = persistenceController.getBoard(boardId).getName();
        return persistenceController.saveLog(boardId,boardName,cardId,rejection.getCardName(),"Project Leader",LogType.REJECTED);

    }

    @RequestMapping(value = "/{logId}/completed",method = RequestMethod.POST)
    public Log changeAcceptedLog(@PathVariable("logId") int logId, @RequestBody CompleteLogOp completeLogOp){
        System.out.println("Mark as completed");
        return persistenceController.setAcceptedFinishedLog(logId,completeLogOp.isAccepted());
    }

}
