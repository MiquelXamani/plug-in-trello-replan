package web.dtos.aux_classes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import web.dtos.Action;
import web.dtos.Card;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookCardTrelloResponse {
    private Action action;
    private Card model;

    public WebhookCardTrelloResponse(){}

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Card getModel() {
        return model;
    }

    public void setModel(Card model) {
        this.model = model;
    }
}
