package web.domain.aux_classes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import web.domain.Action;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookCardTrelloResponse {
    private Action action;

    public WebhookCardTrelloResponse(){}

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
