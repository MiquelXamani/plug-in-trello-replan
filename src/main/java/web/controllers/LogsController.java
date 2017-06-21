package web.controllers;

import web.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import web.LogType;
import web.dtos.aux_classes.CompleteLogOp;
import web.operation_classes.CardDependency;
import web.persistance.models.ResourceMember;
import web.persistance.repositories.ResourceMemberRepository;
import web.domain_controllers.DomainController;
import web.services.ReplanFake;
import web.services.ReplanService;
import web.services.TrelloService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/logs")
public class LogsController {
    private DomainController domainController;
    @Autowired(required = true)
    private ResourceMemberRepository resourceMemberRepository;
    private CardDependency cardDependency;

    @Autowired
    public LogsController(DomainController domainController){
        this.domainController = domainController;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Log> getLogs(@RequestParam(value = "username") String username, @RequestParam(value = "boardId",required=false) String boardId) {
        List<Log> logs;
        //all logs
        if(boardId == null){
            logs = domainController.getLogs(username);
        }
        else{
           logs = domainController.getBoardLogs(boardId);
        }
        Collections.sort(logs);
        return logs;
    }

    @RequestMapping(value = "/reject-card",method = RequestMethod.POST)
    public Log rejectCard(@RequestBody CardRejection rejection){
        System.out.println("Reject process");
        User2 user = domainController.getUser(rejection.getUsername());
        String userToken = user.getTrelloToken();
        String cardId = rejection.getCardId();
        String boardId = rejection.getBoardId();
        TrelloService trelloService = new TrelloService();

        //remove green label
        String purpleLabelId = domainController.getLabelId(boardId,"purple");
        trelloService.removeLabel(cardId,purpleLabelId,userToken);

        //move card to In Progress list and add red label
        String inProgressListId = domainController.getListId(boardId,"In Progress");
        String redLabelId = domainController.getLabelId(boardId,"red");
        List<String> cardsIdList = new ArrayList<>();
        cardsIdList.add(cardId);
        trelloService.moveCardsAndAddLabel(cardsIdList,inProgressListId,redLabelId,userToken);

        //add comment
        trelloService.postComment(cardId,rejection.getComment(),userToken);

        //set previous finished log as rejected
        domainController.setRejectedPreviousFinishedLog(cardId);

        //create log
        String boardName = domainController.getBoard(boardId).getName();
        return domainController.saveLog(cardId,rejection.getCardName(),"Project Leader",LogType.REJECTED);

    }

    @RequestMapping(value = "/{logId}/completed",method = RequestMethod.POST)
    public CardReduced changeAcceptedLog(@PathVariable("logId") int logId, @RequestBody CompleteLogOp completeLogOp){
        System.out.println("Mark as completed");
        return domainController.setAcceptedFinishedLog(logId,completeLogOp.isAccepted());
    }

    @RequestMapping(value = "/replan", method = RequestMethod.POST)
    public String doReplanProvisional(@RequestBody String boardId){
        System.out.println("DO REPLAN");
        List<CompletedJob> completedJobs = new ArrayList<>();
        Map<Integer,String> jobsInfo = domainController.jobsCompletedIds(boardId);
        for (Map.Entry<Integer, String> entry : jobsInfo.entrySet()) {
            completedJobs.add(new CompletedJob(entry.getKey(),entry.getValue()));
        }

        Map<String,String> info = domainController.getBoardReplanInfo(boardId);
        ReplanService replanService = new ReplanService();
        return replanService.doReplan(info.get("endpoint"),Integer.parseInt(info.get("project")),Integer.parseInt(info.get("release")),completedJobs);
    }

    private List<Card> getNextCards(List<Card> cardsUpdated, String boardId, String readyListId) throws ParseException {
        //<memberId, nextcard of this member>
        Map<String,Card> nextCards = new HashMap<>();
        String yellowLabelId = domainController.getLabelId(boardId,"yellow");
        Card currentCard;
        List<String> members;
        Date currentCardStartDate, earliestCardStartDate;
        System.out.println("cardsupdated size:" + cardsUpdated.size());
        for(int i = 0; i < cardsUpdated.size(); i++){
            currentCard = cardsUpdated.get(i);
            if(!currentCard.hasLabel(yellowLabelId)){
                members = currentCard.getIdMembers();
                for(String memberId:members) {
                    if (nextCards.containsKey(memberId)) {
                        currentCardStartDate = currentCard.obtainStartDate();
                        earliestCardStartDate = nextCards.get(memberId).obtainStartDate();
                        if(currentCard.getName().equals("(8) Control sessions") || currentCard.getName().equals("(8) Obtenir informació usuari")) {
                            System.out.println(currentCard.getName()+" current: " + currentCardStartDate + " earlier: " + earliestCardStartDate);
                        }
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
        String greenLabelId = domainController.getLabelId(boardId,"green");
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
    public List<Card> doReplanFake(@RequestBody String boardId) throws ParseException {
        System.out.println("DO REPLAN");
        List<CompletedJob> completedJobs = new ArrayList<>();
        Map<Integer,String> jobsInfo = domainController.jobsCompletedIds(boardId);
        for (Map.Entry<Integer, String> entry : jobsInfo.entrySet()) {
            completedJobs.add(new CompletedJob(entry.getKey(),entry.getValue()));
        }

        String readyListId = domainController.getListId(boardId,"Ready");
        User2 user = domainController.getBoardUser(boardId);

        String inProgressListId = domainController.getListId(boardId,"In Progress");
        TrelloService trelloService = new TrelloService();
        Card[] inProgressCards = trelloService.getListCards(inProgressListId,user.getTrelloToken());
        List<String> cardsInProgressIds = new ArrayList<>();
        for(int i = 0; i < inProgressCards.length; i++){
            cardsInProgressIds.add(inProgressCards[i].getId());
        }
        List<Integer> inProgressJobsIds = domainController.getInProgressJobs(cardsInProgressIds);
        List<InProgressJob> inProgressJobs = new ArrayList<>();
        for(int jid:inProgressJobsIds){
            inProgressJobs.add(new InProgressJob(jid));
        }
        JobsToReplan jobsToReplan = new JobsToReplan(completedJobs,inProgressJobs);

        Map<String,String> info = domainController.getBoardReplanInfo(boardId);
        int endpointId = Integer.parseInt(info.get("endpointId"));
        int projectId = Integer.parseInt(info.get("project"));
        int releaseId = Integer.parseInt(info.get("release"));
        System.out.println("endpointid "+endpointId+" projectid "+projectId+" releaseid "+releaseId);
        ReplanFake replanFake = new ReplanFake(domainController);
        UpdatedPlan updatedPlan = replanFake.doReplanFake(info.get("endpoint"),projectId,releaseId,jobsToReplan);

        //Set cards corresponding to finsihed jobs as replanned
        domainController.setCardsAsReplanned(boardId,new ArrayList<>(jobsInfo.keySet()));


        //<featureId,cardId>, to avoid redundant accesses to db
        Map<Integer,String> featureCardMaps = new HashMap<>();
        //<cardId,jobs of this card>
        Map<String,List<Job>> cardJobsMap = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String onHoldListId = domainController.getListId(boardId,"On-hold");
        String doneListId = domainController.getListId(boardId,"Done");

        List<Job> jobs = updatedPlan.getJobs();
        List<Card> newCards = new ArrayList<>();
        Feature feature;
        String cardId;
        int featureId;
        List<Job> jobList;
        cardDependency = new CardDependency();
        Card[] cardsDone = trelloService.getListCards(doneListId,user.getTrelloToken());
        String yellowLabelId = domainController.getLabelId(boardId,"yellow");
        for(Job job:jobs){
            feature = job.getFeature();
            featureId = feature.getId();
            if(featureCardMaps.containsKey(featureId)){
                System.out.println("està en el map de features (no hauria)");
                cardId = featureCardMaps.get(featureId);
            }
            else{
                cardId = domainController.getCardId(featureId,endpointId,projectId,releaseId);
                //New feature added to release plan, new card will be created
                if(cardId == null){
                    //TODO Save job and card in persistence
                    System.out.println("NOVA FEATURE implica NOVA CARD");
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
                            featureDepending = domainController.getFeature(jr.getFeature_id(),endpointId,projectId,releaseId);
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
        System.out.println("new cards size: "+newCards.size());
        System.out.println("jobs out size: "+updatedPlan.getJobs_out().size());

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
            Date endDate = dateFormat.parse(j.getEnds());
            String endDateString = dateFormat2.format(endDate);
            if(!oldCard.getDue().equals(endDateString)){
                oldCard.setDue(j.getEnds());
            }

            //New start date?
            jobStartDate = dateFormat.parse(j.getStarts());
            if(!oldCard.obtainStartDate().equals(jobStartDate)){
                jobStartDateString = dateFormat2.format(jobStartDate);
                System.out.println(j.getId()+": Modifying b:"+oldCard.obtainStartDate()+" a:"+jobStartDateString);
                oldCard.modifyStartDate(jobStartDateString);
                System.out.println(oldCard.obtainStartDate());
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

        for(Card oc:oldCards){
            System.out.println(oc.getName()+" "+oc.obtainStartDate()+" "+oc.getDue()+" "+oc.getDesc());
        }
        //update cards in Trello
        trelloService.updateCards(updatedCards,user.getTrelloToken());

        //Remove cards
        List<Integer> jobsOut = updatedPlan.getJobs_out();
        String cardOutId;
        for(int jobOutId:jobsOut) {
            cardOutId = domainController.getCardIdOfJob(jobOutId,endpointId,projectId,releaseId);
            //remove from trello
            trelloService.removeCard(cardOutId,user.getTrelloToken());
            //crear log que digui out of release (aquest log es mostrarà a card tracking i no a la taula
        }

        return trelloService.getAllCards(boardId,user.getTrelloToken());
    }

}
