package web.domain.aux_classes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardMovedList {
    IdNameObject listBefore;
    IdNameObject listAfter;

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
}
