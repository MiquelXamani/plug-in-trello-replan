package web.dtos.aux_classes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import web.dtos.Board;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardMovedList {
    IdNameObject listBefore;
    IdNameObject listAfter;
    Board board;

    CardMovedList(){}

    public IdNameObject getListBefore() {
        return listBefore;
    }

    public void setListBefore(IdNameObject listBefore) {
        this.listBefore = listBefore;
    }

    public IdNameObject getListAfter() {
        return listAfter;
    }

    public void setListAfter(IdNameObject listAfter) {
        this.listAfter = listAfter;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
