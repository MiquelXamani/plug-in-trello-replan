package web.domain.aux_classes;

import web.domain.CardTrackingInfo;

import java.util.List;

/**
 * Created by Miquel on 17/05/2017.
 */
public class CardTrackingInfoContainer {
    private String cardId;
    private List<CardTrackingInfo> trackingInfo;

    public CardTrackingInfoContainer(){}

    public CardTrackingInfoContainer(String cardId, List<CardTrackingInfo> trackingInfo) {
        this.cardId = cardId;
        this.trackingInfo = trackingInfo;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public List<CardTrackingInfo> getTrackingInfo() {
        return trackingInfo;
    }

    public void setTrackingInfo(List<CardTrackingInfo> trackingInfo) {
        this.trackingInfo = trackingInfo;
    }
}
