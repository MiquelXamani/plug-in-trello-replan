package web.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Miquel on 15/02/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Webhook {
    public String id;
    public String description;
    public String callbackURL;
    public String idModel; //id de la card, list o board a monitoritzar
    public String active;

    public Webhook(){
    }

    public Webhook(String description, String callbackURL, String idModel){
        this.description = description;
        this.callbackURL = callbackURL;
        this.idModel = idModel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCallbackURL() {
        return callbackURL;
    }

    public void setCallbackURL(String callbackURL) {
        this.callbackURL = callbackURL;
    }

    public String getIdModel() {
        return idModel;
    }

    public void setIdModel(String idModel) {
        this.idModel = idModel;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
