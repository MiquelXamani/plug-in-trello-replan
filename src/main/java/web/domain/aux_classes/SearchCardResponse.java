package web.domain.aux_classes;

import web.domain.Card;

import java.util.ArrayList;
import java.util.List;

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
