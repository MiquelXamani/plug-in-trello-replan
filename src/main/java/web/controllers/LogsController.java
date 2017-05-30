package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.LogType;
import web.domain.*;
import web.domain.aux_classes.CompleteLogOp;
import web.persistance.models.ResourceMember;
import web.persistance.repositories.ResourceMemberRepository;
import web.persistence_controllers.PersistenceController;
import web.services.ReplanService;
import web.services.TrelloService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/logs")
public class LogsController {
    private PersistenceController persistenceController;
    @Autowired(required = true)
    private ResourceMemberRepository resourceMemberRepository;

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
    public String doReplanProvisional(@RequestBody List<Log> logs){
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

    //Will frozen logs passed also?
    @RequestMapping(value = "/replan-fake", method = RequestMethod.POST)
    public void doReplanFake(@RequestBody List<Log> logs) throws ParseException {
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
        UpdatedPlan updatedPlan = replanService.doReplanFake(info.get("endpoint"),Integer.parseInt(info.get("project")),Integer.parseInt(info.get("release")),completedJobs);

        //<featureId,cardId>, to avoid redundant accesses to db
        Map<Integer,String> featureCardMaps = new HashMap<>();
        //<cardId,jobs of this card>
        Map<String,List<Job>> cardJobsMap = new HashMap<>();
        List<Job> jobs = updatedPlan.getJobs();
        Feature feature;
        String cardId;
        int featureId;
        List<Job> jobList;
        for(Job job:jobs){
            feature = job.getFeature();
            featureId = feature.getId();
            if(featureCardMaps.containsKey(featureId)){
                cardId = featureCardMaps.get(featureId);
            }
            else{
                cardId = persistenceController.getCardId(featureId);
                featureCardMaps.put(featureId,cardId);
            }

            if(cardJobsMap.containsKey(cardId)){
                cardJobsMap.get(cardId).add(job);
            }
            else{
                jobList = new ArrayList<>();
                jobList.add(job);
                cardJobsMap.put(cardId,jobList);
            }
        }
        TrelloService trelloService = new TrelloService();
        List<Card> oldCards = trelloService.getCards(new ArrayList<>(cardJobsMap.keySet()),info.get("userToken"));

        //mirar què canvia
        String boardId = logs.get(0).getBoardId();
        User2 user = persistenceController.getBoardUser(boardId);
        String readyListId = persistenceController.getListId(boardId,"Ready");
        //<MemberId, old next CardId>
        Map<String,String> oldNextCards = new HashMap<>();
        List<Job> jobList1;
        List<String> oldMembersList;
        Job j;
        Card oldCard;
        boolean modified;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date jobStartDate;
        String jobStartDateString;
        for(int i = 0; i < oldCards.size(); i++){
            modified = false;
            oldCard = oldCards.get(i);
            oldMembersList = oldCard.getIdMembers();
            if(oldCard.getIdList().equals(readyListId)){
                for(String memberId:oldMembersList) {
                    oldNextCards.put(memberId, oldCard.getId());
                }
            }
            jobList1 = cardJobsMap.get(oldCard.getId());
            j = jobList1.get(0);

            //New due date?
            if(!oldCard.getDue().equals(j.getEnds())){
                oldCard.setDue(j.getEnds());
                modified = true;
            }

            //New start date?
            jobStartDate = dateFormat.parse(j.getStarts());
            jobStartDateString = dateFormat2.format(jobStartDate);
            if(!oldCard.getStartDate().equals(jobStartDateString)){
                oldCard.setStartDate(jobStartDateString);
                modified = true;
            }

            //Members assigned changed?
            //Remove members assigned that are no more assigned to this card
            boolean found;
            for(int k = 0; k < oldMembersList.size(); k++){
                found = false;
                ResourceMember resourceMember = resourceMemberRepository.findByUserIdAndAndTrelloUserId(user.getUserId(),oldMembersList.get(k));
                if(resourceMember != null){
                    int resourceId = resourceMember.getResourceId();
                    for(int l = 0; !found && l < jobList1.size(); l++){
                        if(resourceId == jobList1.get(l).getResource().getId()){
                            found = true;
                        }
                    }
                    if(!found){
                        oldMembersList.remove(k);
                        oldCard.setIdMembers(oldMembersList);
                        modified = true;
                    }
                }
            }

            //Add members that weren't assigned to this card
            for(int k = 0; k < jobList1.size(); k++){
                found = false;
                ResourceMember resourceMember = resourceMemberRepository.findByUserIdAndResourceId(user.getUserId(),jobList1.get(k).getResource().getId());
                if(resourceMember != null) {
                    String trelloId = resourceMember.getTrelloUserId();
                    for (int l = 0; !found && l < oldMembersList.size(); l++) {
                        if (trelloId.equals(oldMembersList.get(l))) {
                            found = true;
                        }
                    }
                    if (!found) {
                        oldMembersList.add(trelloId);
                        oldCard.setIdMembers(oldMembersList);
                        modified = true;
                    }
                }
            }

            if(modified){
                oldCards.set(i,oldCard);
            }
        }

        //Remove cards
        List<Integer> featuresOut = updatedPlan.getFeatures_out();
        String cardOutId;
        for(int featureOutId:featuresOut) {
            cardOutId = persistenceController.getCardId(featureOutId);
            //remove from trello
            trelloService.removeCard(cardOutId,user.getTrelloToken());
            //remove from db ?

        }
    }

}
