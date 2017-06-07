package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.LogType;
import web.domain.*;
import web.domain.aux_classes.CompleteLogOp;
import web.domain.operation_classes.CardDependency;
import web.persistance.models.ResourceMember;
import web.persistance.repositories.ResourceMemberRepository;
import web.persistence_controllers.PersistenceController;
import web.services.ReplanFake;
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
    private CardDependency cardDependency;

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

    private List<Card> getNextCards(List<Card> cardsUpdated, String boardId, String readyListId) throws ParseException {
        //<memberId, nextcard of this member>
        Map<String,Card> nextCards = new HashMap<>();
        String yellowLabelId = persistenceController.getLabelId(boardId,"yellow");
        Card currentCard;
        List<String> members;
        Date currentCardStartDate, earliestCardStartDate;
        for(int i = 0; i < cardsUpdated.size(); i++){
            currentCard = cardsUpdated.get(i);
            if(!currentCard.hasLabel(yellowLabelId)){
                members = currentCard.getIdMembers();
                for(String memberId:members) {
                    if (nextCards.containsKey(memberId)) {
                        currentCardStartDate = currentCard.obtainStartDate();
                        earliestCardStartDate = nextCards.get(memberId).obtainStartDate();
                        if(earliestCardStartDate.after(currentCardStartDate)){
                            nextCards.put(memberId,currentCard);
                        }
                    }
                    else {
                        nextCards.put(memberId,currentCard);
                    }
                }
            }
        }
        Card card;
        String greenLabelId = persistenceController.getLabelId(boardId,"green");
        for (int j = 0; j < cardsUpdated.size(); j++) {
            card = cardsUpdated.get(j);
            if(nextCards.containsValue(card)){
                System.out.println("Next card: " + card.getName());
                card.addLabel(greenLabelId);
                card.setIdList(readyListId);
                cardsUpdated.set(j,card);
            }
        }
        return cardsUpdated;
    }

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

        String boardId = logs.get(0).getBoardId();
        String readyListId = persistenceController.getListId(boardId,"Ready");
        User2 user = persistenceController.getBoardUser(boardId);

        String inProgressListId = persistenceController.getListId(boardId,"In Progress");
        TrelloService trelloService = new TrelloService();
        Card[] inProgressCards = trelloService.getListCards(inProgressListId,user.getTrelloToken());
        List<String> cardsInProgressIds = new ArrayList<>();
        for(int i = 0; i < inProgressCards.length; i++){
            cardsInProgressIds.add(inProgressCards[i].getId());
        }
        List<Integer> inProgressJobsIds = persistenceController.getInProgressJobs(cardsInProgressIds);
        List<InProgressJob> inProgressJobs = new ArrayList<>();
        for(int jid:inProgressJobsIds){
            inProgressJobs.add(new InProgressJob(jid));
        }
        JobsToReplan jobsToReplan = new JobsToReplan(completedJobs,inProgressJobs);

        Map<String,String> info = persistenceController.getBoardReplanInfoFromLogId(logs.get(0).getId());
        int endpointId = Integer.parseInt(info.get("endpointId"));
        int projectId = Integer.parseInt(info.get("project"));
        int releaseId = Integer.parseInt(info.get("release"));
        ReplanFake replanFake = new ReplanFake(persistenceController);
        UpdatedPlan updatedPlan = replanFake.doReplanFake(info.get("endpoint"),projectId,releaseId,jobsToReplan);

        //<featureId,cardId>, to avoid redundant accesses to db
        Map<Integer,String> featureCardMaps = new HashMap<>();
        //<cardId,jobs of this card>
        Map<String,List<Job>> cardJobsMap = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String onHoldListId = persistenceController.getListId(boardId,"On-hold");
        String doneListId = persistenceController.getListId(boardId,"Done");

        List<Job> jobs = updatedPlan.getJobs();
        List<Card> newCards = new ArrayList<>();
        Feature feature;
        String cardId;
        int featureId;
        List<Job> jobList;
        cardDependency = new CardDependency();
        Card[] cardsDone = trelloService.getListCards(doneListId,user.getTrelloToken());
        String yellowLabelId = persistenceController.getLabelId(boardId,"yellow");
        for(Job job:jobs){
            feature = job.getFeature();
            featureId = feature.getId();
            if(featureCardMaps.containsKey(featureId)){
                cardId = featureCardMaps.get(featureId);
            }
            else{
                cardId = persistenceController.getCardId(featureId,endpointId,projectId,releaseId);
                //New feature added to release plan, new card will be created
                if(cardId == null){
                    Card newCard = new Card();
                    String name = "("+ Math.round(feature.getEffort()) +") " + feature.getName();
                    newCard.setName(name);
                    newCard.setDue(job.getEnds());
                    ResourceMember resourceMember = resourceMemberRepository.findByUserIdAndResourceId(user.getUserId(),job.getResource().getId());
                    if(resourceMember != null) {
                        newCard.addMember(resourceMember.getTrelloUserId());
                    }
                    Date startDate = dateFormat.parse(job.getStarts());
                    String description = feature.getDescription() + "\n\n";
                    description += "**Start date:** " + dateFormat2.format(startDate) + "\n**Depends on:**";
                    int dependsOnSize = job.getDepends_on().size();
                    if( dependsOnSize == 0){
                        description += " -";
                    }
                    else {
                        int count = 0;
                        Feature featureDepending;
                        for (JobReduced jr : job.getDepends_on()) {
                            featureDepending = persistenceController.getFeature(jr.getFeature_id(),endpointId,projectId,releaseId);
                            description += " ("+ Math.round(featureDepending.getEffort()) +") " + featureDepending.getName();
                            if(count < dependsOnSize - 1) {
                                description += " ,";
                            }
                            ++count;
                        }
                    }
                    newCard.setDesc(description);
                    newCard.setIdList(onHoldListId);
                    if(cardDependency.stillDependsOnAnotherCard(newCard,cardsDone)){
                        newCard.addLabel(yellowLabelId);
                    }
                    newCards.add(newCard);
                }
                else {
                    featureCardMaps.put(featureId, cardId);
                }
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

        //create new cards
        newCards = trelloService.createCards(newCards,user.getTrelloToken());

        //List<Card> oldCards = trelloService.getCards(new ArrayList<>(cardJobsMap.keySet()),user.getTrelloToken());
        List<Card> allCards = trelloService.getAllCards(boardId,user.getTrelloToken());
        List<Card> oldCards = new ArrayList<>();
        for(Card boardCard:allCards){
            if(cardJobsMap.containsKey(boardCard.getId())){
                oldCards.add(boardCard);
            }
        }

        //mirar què canvia
        List<Job> jobList1;
        List<String> oldMembersList;
        Job j;
        Card oldCard;
        Date jobStartDate;
        String jobStartDateString;
        for(int i = 0; i < oldCards.size(); i++){
            oldCard = oldCards.get(i);
            oldMembersList = oldCard.getIdMembers();
            //reset NextCard in order to recalculate it later
            if(oldCard.getIdList().equals(readyListId)){
                //Move provisionally to On-hold
                oldCard.setIdList(onHoldListId);
                //Remove labels
                oldCard.setIdLabels(new ArrayList<>());
            }

            jobList1 = cardJobsMap.get(oldCard.getId());
            j = jobList1.get(0);

            //New due date?
            if(!oldCard.getDue().equals(j.getEnds())){
                oldCard.setDue(j.getEnds());
            }

            //New start date?
            jobStartDate = dateFormat.parse(j.getStarts());
            jobStartDateString = dateFormat2.format(jobStartDate);
            if(!oldCard.obtainStartDate().equals(jobStartDateString)){
                oldCard.modifyStartDate(jobStartDateString);
            }

            //Members assigned changed?
            //Remove members assigned that are no more assigned to this card
            boolean found;
            int resourceId;
            for(int k = 0; k < oldMembersList.size(); k++){
                found = false;
                ResourceMember resourceMember = resourceMemberRepository.findByUserIdAndAndTrelloUserId(user.getUserId(),oldMembersList.get(k));
                if(resourceMember != null){
                    resourceId = resourceMember.getResourceId();
                    for(int l = 0; !found && l < jobList1.size(); l++){
                        if(resourceId == jobList1.get(l).getResource().getId()){
                            found = true;
                        }
                    }
                    if(!found){
                        oldMembersList.remove(k);
                        oldCard.setIdMembers(oldMembersList);
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
                    }
                }
            }
            oldCards.set(i,oldCard);
        }

        //afegir les cards de la llista newcards a oldcards per poder calcular la next card
        oldCards.addAll(newCards);
        //recalculate nextcards after replan
        List<Card> updatedCards = getNextCards(oldCards,boardId,readyListId);
        //update cards in Trello
        trelloService.updateCards(updatedCards,user.getTrelloToken());

        //Remove cards
        List<Integer> jobsOut = updatedPlan.getJobs_out();
        String cardOutId;
        for(int jobOutId:jobsOut) {
            cardOutId = persistenceController.getCardIdOfJob(jobOutId,endpointId,projectId,releaseId);
            //remove from trello
            trelloService.removeCard(cardOutId,user.getTrelloToken());
            //crear log que digui out of release (aquest log es mostrarà a card tracking i no a la taula
        }
    }

}
