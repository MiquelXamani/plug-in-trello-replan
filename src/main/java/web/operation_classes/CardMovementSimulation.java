package web.operation_classes;


import org.springframework.beans.factory.annotation.Autowired;
import web.dtos.Card;
import web.dtos.Member;
import web.dtos.TeamWithMembers;
import web.domain_controllers.DomainController;
import web.services.TrelloService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardMovementSimulation {
    private TrelloService trelloService;
    private DomainController domainController;

    @Autowired
    public CardMovementSimulation(DomainController domainController){
        this.domainController = domainController;
        trelloService = new TrelloService();
    }

    /*case to simulate:
        1: Feature out of plan
        2: New feature added to plan
        3: Dates change, start date and end date of involved features suffer a delay of 1 day
        4: Resource assigned to a feature changed
        5: Next card assigned to a resource changed

     */
    public void simulateMovement(int caseToSimulate, List<Card> cards, String boardId, String userToken) throws InterruptedException {
        //Team ecommerce llibres
        System.out.println("Test feature out");
        String teamId = "593570a6e779fbbd879b0278";
        TeamWithMembers teamWithMembers = trelloService.getTrelloTeamMembers(teamId,userToken);
        List<Member> members = teamWithMembers.getMembers();
        String josepId, albertId, pereId, mariaId, sergiId;
        String josepToken = "7cd729590c8d9ea48339b61ad5eb511ecfc66c3b62ea9c5cb896987f94b77ead";
        String albertToken = "9a1ad1d2dc10859c4968295f06f848cf8ccfbcb4de15307eae84ef8ffa55cdc8";
        String pereToken = "c62fa6953152edbc8612a402f1b84e08c1724295e95af1354e9d8e5de5d16de5";
        String mariaToken = "32726636750f565b014498e37738a150c111693debcd84a2db201bc6f1d7cf5e";
        String sergiToken = "fd4ad63325314d6300366ef2353a989307afff8f3a19b594e3ee89283c4b3beb";
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

        String doneListId = domainController.getListId(boardId,"Done");
        String inProgressListId = domainController.getListId(boardId,"In Progress");
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
                Thread.sleep(1500);
                trelloService.moveCard(card.getId(),doneListId,mariaToken);
                card = cardMap.get("(4) FAQ");
                trelloService.moveCard(card.getId(),inProgressListId,mariaToken);
                card = cardMap.get("(4) FAQ");
                trelloService.moveCard(card.getId(),doneListId,mariaToken);
                card = cardMap.get("(8) Maquetació parts comunes");
                trelloService.moveCard(card.getId(),doneListId,sergiToken);
                card = cardMap.get("(16) Configuració inicial servidor");
                trelloService.moveCard(card.getId(),doneListId,josepToken);
                Thread.sleep(1500);
                card = cardMap.get("(8) Configuració inicial BD");
                trelloService.moveCard(card.getId(),inProgressListId,josepToken);
                card = cardMap.get("(8) Configuració inicial BD");
                trelloService.moveCard(card.getId(),doneListId,josepToken);
                Thread.sleep(1500);
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
