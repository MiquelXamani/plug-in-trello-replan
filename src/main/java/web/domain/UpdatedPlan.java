package web.domain;

import java.util.List;

/**
 * Provisional class
 * Replan return isn't defined yet
 */
public class UpdatedPlan extends Plan {
    List<Integer> features_out;

    public UpdatedPlan(){}

    public List<Integer> getFeatures_out() {
        return features_out;
    }

    public void setFeatures_out(List<Integer> features_out) {
        this.features_out = features_out;
    }
}
