package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.LogType;
import web.domain.*;
import web.domain.aux_classes.CompleteLogOp;
import web.persistence_controllers.PersistenceController;
import web.services.ReplanService;
import web.services.TrelloService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @RequestMapping(value = "/replan", method = RequestMethod.POST)
    public String doReplan(@RequestBody List<Log> logs){
        System.out.println("DO REPLAN");
        List<CompletedJob> completedJobs = new ArrayList<>();
        List<Integer> jobsIds;
        for (Log log:logs) {
            jobsIds = persistenceController.getJobsIdsLog(log.getId());
            for(int jobId:jobsIds){
                completedJobs.add(new CompletedJob(jobId,log.getCreatedAt()));
            }
        }
        Map<String,String> info = persistenceController.getBoardReplanInfoFromLogId(logs.get(0).getId());
        ReplanService replanService = new ReplanService();
        return replanService.doReplan(info.get("endpoint"),Integer.parseInt(info.get("project")),Integer.parseInt(info.get("release")),completedJobs);
    }


}
