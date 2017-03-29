package web.models;

import java.util.List;

/**
 * Created by Miquel on 29/03/2017.
 */
public class PlanTrello {
    private Board board;
    private List<ListTrello> lists;
    private List<Card> cards;

    public PlanTrello(){

    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public List<ListTrello> getLists() {
        return lists;
    }

    public void setLists(List<ListTrello> lists) {
        this.lists = lists;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}
