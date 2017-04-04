package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import web.models.*;
import web.repositories.BoardRepository;
import web.repositories.ListTrelloRepository;
import web.repositories.ResourceMemberRepository;
import web.repositories.UserRepository;
import web.services.TrelloService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/boards")
public class BoardController {
    @Autowired(required = true)
    private UserRepository userRepository;
    @Autowired(required = true)
    private BoardRepository boardRepository;
    @Autowired(required = true)
    private ListTrelloRepository listTrelloRepository;
    @Autowired(required = true)
    private ResourceMemberRepository resourceMemberRepository;

    @RequestMapping(method= RequestMethod.POST)
    public ResponseEntity<PlanTrello> createBoard(@RequestBody PlanBoardDTO planBoardDTO){
        System.out.println("BOARD CONTROLLER REQUEST RECEIVED");
        User u = userRepository.findByUsername(planBoardDTO.getUsername());
        String trelloToken = u.getTrelloToken();
        String trelloUserIdWebUser = u.getTrelloUserId();
        TrelloService trelloService = new TrelloService();

        //Create board
        Board board = trelloService.createBoard(planBoardDTO.getName(),planBoardDTO.getBoardName(),planBoardDTO.getTeamId(),trelloToken);
        boardRepository.save(board);
        String boardId = board.getId();
        System.out.println("Board id: " + boardId);

        //Create lists
        ListTrello ls[] = trelloService.createLists(boardId,trelloToken);
        List<ListTrello> lists = new ArrayList<>(Arrays.asList(ls));
        listTrelloRepository.save(lists);

        //Get labels id
        Label[] labels = trelloService.getLabels(boardId,trelloToken);
        //For cards which can be started now
        Label greenLabel = new Label();
        //For notification cards
        Label blueLabel = new Label();
        String labelColor;
        for(Label l: labels){
            labelColor = l.getColor();
            if(labelColor.equals("green")){
                greenLabel = l;
            }
            else if(labelColor.equals("blue")){
                blueLabel = l;
            }
        }
        System.out.println("Green label: " + greenLabel.getId() + " " + greenLabel.getColor());
        System.out.println("Blue label: " + blueLabel.getId() + " " + blueLabel.getColor());


        List<Job> jobs = planBoardDTO.getJobs();
        //Map of resourceId and trelloUserId
        Map<Integer,String> resourcesAddedBoard = new HashMap<>();
        //Map of featureId and card corresponding to the feature
        Map<Integer,Card> featuresConverted = new HashMap<>();

        //Map to store the first job to start of each member
        Map<String,Job> firstsJobs = new HashMap<>();
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        int resourceId, featureId;
        Card card;
        Feature feature;
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

            feature = j.getFeature();
            featureId = feature.getId();
            //si el recurs ja està en el map, es recupera
            if(trelloUserId.equals("")){
                trelloUserId = resourcesAddedBoard.get(resourceId);
            }

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
                String description = feature.getDescription() + "\n\n";
                description += "**Start date:** " + j.getStarts() + "\n**Depends on:**";
                if(j.getDepends_on().size() == 0){
                    description += " -";
                }
                else {
                    for (Job j2 : j.getDepends_on()) {
                        description += " " + j2.getFeature().getName();
                    }
                }
                card.setDesc(description);
                featuresConverted.put(featureId,card);
            }

            //IMPORTANT: firstjobs are related to trello users, no green labels will be added to those cards that are "first job" only for nonassociated resources
            if(!trelloUserId.equals("") && firstsJobs.containsKey(trelloUserId)){
                try {
                    Job j3 = firstsJobs.get(trelloUserId);
                    String dateAnt = j3.getStarts();
                    Date d1 = dateFormat.parse(dateAnt);
                    Date d2 = dateFormat.parse(j.getStarts());
                    //if d2 is earlier than d1, replace
                    if(d2.compareTo(d1) <= 0){
                        firstsJobs.put(trelloUserId,j);
                    }

                }
                catch (ParseException e){
                    e.printStackTrace();
                }
            }
            else{
                firstsJobs.put(trelloUserId,j);
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
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        notification.setName("[" + dateFormat2.format(date) + "] Planification loaded");
        notification.addLabel(blueLabel.getId());
        notification.setPos("top");
        notification.setDesc("Notification card");
        notification.setIdList(lists.get(0).getId());

        //Create cards on Trello
        List<Card> cards = new ArrayList<>(featuresConverted.values());
        cards.add(notification);
        trelloService.createCards(cards,trelloToken);

        PlanTrello result = new PlanTrello();
        result.setBoard(board);
        result.setLists(lists);
        result.setCards(cards);

        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
