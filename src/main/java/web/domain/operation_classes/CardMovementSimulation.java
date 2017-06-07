package web.domain.operation_classes;


import org.springframework.beans.factory.annotation.Autowired;
import web.domain.Card;
import web.domain.Member;
import web.domain.Plan;
import web.domain.TeamWithMembers;
import web.persistence_controllers.PersistenceController;
import web.services.TrelloService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardMovementSimulation {
    private TrelloService trelloService;
    private PersistenceController persistenceController;

    @Autowired
    public CardMovementSimulation(PersistenceController persistenceController){
        this.persistenceController = persistenceController;
        trelloService = new TrelloService();
    }

    /*case to simulate:
        1: Feature out of plan
        2: New feature added to plan
        3: Dates change, start date and end date of involved features suffer a delay of 1 day
        4: Resource assigned to a feature changed
        5: Next card assigned to a resource changed

     */
    public void simulateMovement(int caseToSimulate, List<Card> cards, String boardId, String userToken){
        //Team ecommerce llibres
        System.out.println("Test feature out");
        String teamId = "593570a6e779fbbd879b0278";
        TeamWithMembers teamWithMembers = trelloService.getTrelloTeamMembers(teamId,userToken);
        List<Member> members = teamWithMembers.getMembers();
        String josepId, albertId, pereId, mariaId, sergiId;
        String josepToken = userToken;
        String albertToken = userToken;
        String pereToken = userToken;
        String mariaToken = userToken;
        String sergiToken = userToken;
        for (Member m:members) {
            switch(m.getUsername()){
                case "josep248":
                    josepId = m.getId();
                    break;
                case "albert50841018":
                    albertId = m.getId();
                    break;
                case "pere147":
                    pereId = m.getId();
                    break;
                case "maria56562681":
                    mariaId = m.getId();
                    break;
                case "sergi331":
                    sergiId = m.getId();
                    break;
                default:
                    break;
            }
        }

        Map<String,Card> cardMap = new HashMap<>();
        for(Card card:cards){
            cardMap.put(card.getName(),card);
        }

        String doneListId = persistenceController.getListId(boardId,"Done");
        String inProgressListId = persistenceController.getListId(boardId,"In Progress");
        Card card;

        switch(caseToSimulate){
            case 1:
                card = cardMap.get("(16) Configuració inicial servidor");
                trelloService.moveCard(card.getId(),inProgressListId,josepToken);
                card = cardMap.get("(4) Pàgina de contacte");
                trelloService.moveCard(card.getId(),inProgressListId,mariaToken);
                card = cardMap.get("(8) Maquetació parts comunes");
                trelloService.moveCard(card.getId(),inProgressListId,sergiToken);
                card = cardMap.get("(4) Pàgina de contacte");
                trelloService.moveCard(card.getId(),doneListId,mariaToken);
                card = cardMap.get("(4) FAQ");
                trelloService.moveCard(card.getId(),inProgressListId,mariaToken);
                card = cardMap.get("(4) FAQ");
                trelloService.moveCard(card.getId(),doneListId,mariaToken);
                card = cardMap.get("(8) Maquetació parts comunes");
                trelloService.moveCard(card.getId(),doneListId,sergiToken);
                card = cardMap.get("(16) Configuració inicial servidor");
                trelloService.moveCard(card.getId(),doneListId,josepToken);
                card = cardMap.get("(8) Configuració inicial BD");
                trelloService.moveCard(card.getId(),inProgressListId,josepToken);
                card = cardMap.get("(8) Configuració inicial BD");
                trelloService.moveCard(card.getId(),doneListId,josepToken);
                card = cardMap.get("(16) Disseny BD");
                trelloService.moveCard(card.getId(),inProgressListId,josepToken);
                card = cardMap.get("(16) Disseny BD");
                trelloService.moveCard(card.getId(),doneListId,josepToken);
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            default:
                break;
        }
    }
}
