package web.domain.aux_classes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import web.domain.Card;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchCardResponse {
    private List<Card> cards;

    public SearchCardResponse(){
        this.cards = new ArrayList<>();
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}
