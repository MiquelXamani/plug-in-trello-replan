package web.domain.aux_classes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import web.domain.Action;
import web.domain.Card;

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
