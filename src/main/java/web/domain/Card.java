package web.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Miquel on 13/02/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Card {
    public String id;
    public String name;
    public String idList;
    public String due;
    public String desc;
    public List<String> idMembers;
    public List<String> idLabels;
    public String pos;

    public Card(){
        idMembers = new ArrayList<>();
        idLabels = new ArrayList<>();
        pos = "bottom";
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

    public String getDue() {
        return due;
    }

    public void setDue(String due) {
        this.due = due;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getIdMembers() {
        return idMembers;
    }

    public void setIdMembers(List<String> idMembers) {
        this.idMembers = idMembers;
    }

    public List<String> getIdLabels() {
        return idLabels;
    }

    public void setIdLabels(List<String> idLabels) {
        this.idLabels = idLabels;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public void addMember(String idMember){
        idMembers.add(idMember);
    }

    public void addLabel(String idLabel){
        idLabels.add(idLabel);
    }

    public boolean hasLabel(String idLabel){
        boolean found = false;
        for(int i = 0; !found && i < idLabels.size(); i++){
            if(idLabels.get(i).equals(idLabel)){
                found = true;
            }
        }
        return found;
    }

    public boolean isAssigned(String idMember){
        boolean found = false;
        for(int i = 0; !found && i < idMembers.size(); i++){
            if(idMembers.get(i).equals(idMember)){
                found = true;
            }
        }
        return found;
    }

    public Date getStartDate() throws ParseException {
        String date;
        String startDateText = "**Start date:** ";
        int textLength = startDateText.length();
        String datePattern = "yyyy/MM/dd HH:mm:ss";
        int dateLength = datePattern.length();
        int startDateTextIndex, startDateValueIndex, startDateValueIndexFinal;
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
        startDateTextIndex = desc.indexOf(startDateText);
        startDateValueIndex = startDateTextIndex + textLength;
        startDateValueIndexFinal = startDateValueIndex + dateLength;
        date = desc.substring(startDateValueIndex, startDateValueIndexFinal);
        return dateFormat.parse(date);
    }

    public void setStartDate(String startDate) throws ParseException {
        Date d = getStartDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String oldStartDate = dateFormat.format(d);
        desc.replaceAll(oldStartDate,startDate);
    }
}
