package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import web.domain.CardTrackingInfo;
import web.domain.aux_classes.CardTrackingInfoContainer;
import web.persistence_controllers.PersistenceController;

import java.util.List;

@RestController
@RequestMapping("/card")
public class CardsController {
    private PersistenceController persistenceController;

    @Autowired
    public CardsController(PersistenceController persistenceController){
        this.persistenceController = persistenceController;
    }

    @RequestMapping(value = "/tracking/{cardId}",method = RequestMethod.GET)
    public CardTrackingInfoContainer getCardTrackingInfo(@PathVariable(value = "cardId") String cardId) {
        System.out.println("GET card tracking info");
        List<CardTrackingInfo> cardTrackingInfoList = persistenceController.getCardTrackingInfo(cardId);
        return new CardTrackingInfoContainer(cardId,cardTrackingInfoList);
    }
}
