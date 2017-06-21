package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import web.dtos.CardTrackingInfo;
import web.dtos.aux_classes.CardTrackingInfoContainer;
import web.domain_controllers.DomainController;

import java.util.List;

@RestController
@RequestMapping("/card")
public class CardsController {
    private DomainController domainController;

    @Autowired
    public CardsController(DomainController domainController){
        this.domainController = domainController;
    }

    @RequestMapping(value = "/tracking/{cardId}",method = RequestMethod.GET)
    public CardTrackingInfoContainer getCardTrackingInfo(@PathVariable(value = "cardId") String cardId) {
        System.out.println("GET card tracking info");
        List<CardTrackingInfo> cardTrackingInfoList = domainController.getCardTrackingInfo(cardId);
        return new CardTrackingInfoContainer(cardId,cardTrackingInfoList);
    }
}
