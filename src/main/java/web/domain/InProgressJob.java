package web.domain;

/**
 * Created by Miquel on 31/05/2017.
 */
public class InProgressJob {
    private int job_id;

    public InProgressJob(int job_id) {
        this.job_id = job_id;
    }

    public int getJob_id() {
        return job_id;
    }

    public void setJob_id(int job_id) {
        this.job_id = job_id;
    }
}
