package web.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Miquel on 15/02/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Webhook {
    public String description;
    public String callbackURL;
    public String idModel; //id de la card, list o board a monitoritzar

    public Webhook(){
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
}
