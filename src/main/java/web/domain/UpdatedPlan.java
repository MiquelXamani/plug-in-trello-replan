package web.domain;

import java.util.List;

/**
 * Provisional class
 * Replan return isn't defined yet
 */
public class UpdatedPlan extends Plan {
    List<Integer> jobs_out;

    public UpdatedPlan(){}

    public List<Integer> getJobs_out() {
        return jobs_out;
    }

    public void setJobs_out(List<Integer> jobs_out) {
        this.jobs_out = jobs_out;
    }
}
