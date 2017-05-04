package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.domain.*;
import web.persistance.repositories.*;
import web.persistance.models.ResourceMember;
import web.persistence_controllers.PersistenceController;
import web.services.TrelloService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/boards")
public class BoardsController {
    @Autowired(required = true)
    private ResourceMemberRepository resourceMemberRepository;
    private PersistenceController persistenceController;

    @Autowired
    public BoardsController(PersistenceController persistenceController){
        this.persistenceController = persistenceController;
    }

    @RequestMapping(method= RequestMethod.POST)
    public ResponseEntity<PlanTrello> createBoard(@RequestBody PlanBoardDTO planBoardDTO) throws ParseException {
        System.out.println("BOARD CONTROLLER REQUEST RECEIVED");
        User2 u = persistenceController.getUser(planBoardDTO.getUsername());
        System.out.println(planBoardDTO.getUsername());
        System.out.println(u.getTrelloToken());
        String trelloToken = u.getTrelloToken();
        String trelloUserIdWebUser = u.getTrelloUserId();
        TrelloService trelloService = new TrelloService();

        //Create board
        Board board = trelloService.createBoard(planBoardDTO.getBoardName(),planBoardDTO.getTeamId(),trelloToken);
        String boardId = board.getId();
        System.out.println("Board id: " + boardId);

        //Create lists
        ListTrello ls[] = trelloService.createLists(boardId,trelloToken);
        List<ListTrello> lists = new ArrayList<>(Arrays.asList(ls));

        //Get labels id
        Label[] labels = trelloService.getLabels(boardId,trelloToken);
        //For cards which can be started now
        Label greenLabel = new Label();
        //For notification cards
        Label blueLabel = new Label();
        //For cards done
        Label purpleLabel;
        String labelColor;
        List<Label> labelList = new ArrayList<>();
        for(Label l: labels){
            labelColor = l.getColor();
            if(labelColor.equals("green")){
                greenLabel = l;
                labelList.add(greenLabel);
            }
            else if(labelColor.equals("blue")){
                blueLabel = l;
                labelList.add(blueLabel);
            }
            else if(labelColor.equals("purple")){
                purpleLabel = l;
                labelList.add(purpleLabel);
            }
        }

        //persistence
        persistenceController.saveBoard(board,labelList,lists,u);

        List<Job> jobs = planBoardDTO.getJobs();
        //Map of resourceId and trelloUserId
        Map<Integer,String> resourcesAddedBoard = new HashMap<>();
        //Map of featureId and card corresponding to the feature
        Map<Integer,Card> featuresConverted = new HashMap<>();

        //Map to store the first job to start of each member
        Map<String,Job> firstsJobs = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        int resourceId, featureId;
        Card card;
        Feature feature;
        Date startDate;
        //Only test
        int countTest = 0;
        String testname = "";
        double testEffort = 0;

        for (Job j: jobs) {
            resourceId = j.getResource().getId();
            String trelloUserId = "";
            //Add member associated with the resource to the board
            if(!resourcesAddedBoard.containsKey(resourceId)){
                ResourceMember resourceMember = resourceMemberRepository.findByUserIdAndResourceId(u.getUserId(),resourceId);
                if(resourceMember != null){
                    trelloUserId = resourceMember.getTrelloUserId();
                    if(!trelloUserId.equals(trelloUserIdWebUser)) {
                        //si es dóna el cas que l'usuari de la web està a la planificació, no es pot convidar al board ja que n'és el propietari
                        trelloService.addMemberToBoard(boardId, trelloUserId, trelloToken);
                    }
                }
                resourcesAddedBoard.put(resourceId,trelloUserId);
            }
            else{
                trelloUserId = resourcesAddedBoard.get(resourceId);
            }

            feature = j.getFeature();
            featureId = feature.getId();

            //Add member to a card if already exists a card for the feature
            if(featuresConverted.containsKey(featureId)){
                card = featuresConverted.get(featureId);
                //si el recurs està associat a un usuari s'assigna a la card
                if(!trelloUserId.equals("")) {
                    card.addMember(trelloUserId);
                }
            }
            //Otherwise, create a card for the feature
            else{
                card = new Card();
                String name = "("+ Math.round(feature.getEffort()) +") " + feature.getName();

                //ONLY TEST second and third cards depends on first card
                if(countTest == 0){
                    testname = name;
                    testEffort = feature.getEffort();
                }
                else if(countTest < 3 && countTest > 0){
                    Feature featureTest = new Feature();
                    featureTest.setName(testname);
                    featureTest.setEffort(testEffort);
                    Job jobTest = new Job();
                    jobTest.setFeature(featureTest);
                    j.getDepends_on().add(jobTest);
                }
                ++countTest;

                //finish test code

                card.setName(name);
                card.setDue(feature.getDeadline());
                //si el recurs està associat a un usuari s'assigna a la card
                if(!trelloUserId.equals("")) {
                    card.addMember(trelloUserId);
                }
                if(j.getDepends_on().isEmpty()){
                    //Ready
                    card.setIdList(lists.get(2).getId());
                }
                else{
                    //On-hold
                    card.setIdList(lists.get(1).getId());
                }

                startDate = dateFormat.parse(j.getStarts());
                String description = feature.getDescription() + "\n\n";
                description += "**Start date:** " + dateFormat2.format(startDate) + "\n**Depends on:**";
                int dependsOnSize = j.getDepends_on().size();
                if( dependsOnSize == 0){
                    description += " -";
                }
                else {
                    int count = 0;
                    for (Job j2 : j.getDepends_on()) {
                        description += " ("+ Math.round(j2.getFeature().getEffort()) +") " + j2.getFeature().getName();
                        if(count < dependsOnSize - 1) {
                            description += " ,";
                        }
                        ++count;
                    }
                }
                card.setDesc(description);
                featuresConverted.put(featureId,card);
            }

            //IMPORTANT: firstjobs are related to trello users, no green labels will be added to those cards that are "first job" only of nonassociated resources
            if(!trelloUserId.equals("")) {
                if (firstsJobs.containsKey(trelloUserId)) {
                    Job j3 = firstsJobs.get(trelloUserId);
                    String dateAnt = j3.getStarts();
                    Date d1 = dateFormat.parse(dateAnt);
                    Date d2 = dateFormat.parse(j.getStarts());
                    //if d2 is earlier than d1, replace
                    if (d2.compareTo(d1) <= 0) {
                        firstsJobs.put(trelloUserId, j);
                    }
                }
                else {
                    firstsJobs.put(trelloUserId, j);
                }
            }

        }

        //add green labels to cards that are the first job of at least one user
        boolean greenLabelFound;
        int i;
        String greenLabelId = greenLabel.getId();
        List <String> labels2;
        Card c;
        for(Job value: firstsJobs.values()){
            featureId = value.getFeature().getId();
            greenLabelFound = false;
            i = 0;
            c = featuresConverted.get(featureId);
            //only cards of Ready list can have green label
            if(c.getIdList().equals(lists.get(2).getId())) {
                labels2 = c.getIdLabels();
                while (!greenLabelFound && i < labels2.size()) {
                    if (labels2.get(i).equals(greenLabelId)) {
                        greenLabelFound = true;
                    }
                    i++;
                }

                if (!greenLabelFound) {
                    featuresConverted.get(featureId).addLabel(greenLabel.getId());
                    featuresConverted.get(featureId).setPos("top");
                }
            }
        }

        //Notification card
        Card notification = new Card();
        Date date = new Date();
        notification.setName("[" + dateFormat2.format(date) + "] Planification loaded");
        notification.addLabel(blueLabel.getId());
        notification.setPos("top");
        notification.setDesc("Notification card");
        notification.setIdList(lists.get(0).getId());

        //Create cards on Trello
        List<Card> cards = new ArrayList<>(featuresConverted.values());
        cards.add(notification);
        List<Card> createdCards = trelloService.createCards(cards,trelloToken);

        //Create webhooks for each card to track
        trelloService.createWebhooks(createdCards,trelloToken);

        PlanTrello result = new PlanTrello();
        result.setBoard(board);
        result.setLists(lists);
        result.setCards(cards);

        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Board> getBoards(@RequestParam(value = "username") String username) {
        return persistenceController.getUserBoards(username);
    }
}
