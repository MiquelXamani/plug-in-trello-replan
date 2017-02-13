package hello;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miquel on 13/02/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Card {
    public String id;
    public String name;
    public String idList;
    //public List<String> idMembers; No va perquè no pot desserialitzar l'array. En el Json és un array de Strings
    //"idMembers":["5870fd94eabd62f19de4ef5f","585bec12bec126f26851c2fb"]
    public String idBoard;

    public Card(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdList() {
        return idList;
    }

    public void setIdList(String idList) {
        this.idList = idList;
    }

    /*public List<String> getIdMembers() {
        return idMembers;
    }

    public void setIdMembers(String idMembers) {
        this.idMembers.add(idMembers);
    }*/

    public String getIdBoard() {
        return idBoard;
    }

    public void setIdBoard(String idBoard) {
        this.idBoard = idBoard;
    }
}
