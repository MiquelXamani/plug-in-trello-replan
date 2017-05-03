package web.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import web.domain.aux_classes.CardMovedList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Action {
    private String idMemberCreator;
    private String type;
    private CardMovedList data;
    private Member memberCreator;

    public Action(){}

    public String getIdMemberCreator() {
        return idMemberCreator;
    }

    public void setIdMemberCreator(String idMemberCreator) {
        this.idMemberCreator = idMemberCreator;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CardMovedList getData() {
        return data;
    }

    public void setData(CardMovedList data) {
        this.data = data;
    }

    public Member getMemberCreator() {
        return memberCreator;
    }

    public void setMemberCreator(Member memberCreator) {
        this.memberCreator = memberCreator;
    }
}
