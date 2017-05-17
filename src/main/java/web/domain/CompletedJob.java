package web.domain;

/**
 * Created by Miquel on 17/05/2017.
 */
public class CompletedJob {
    private int job_id;
    private String completed_at;

    public CompletedJob(){}

    public CompletedJob(int job_id, String completed_at) {
        this.job_id = job_id;
        this.completed_at = completed_at;
    }

    public int getJob_id() {
        return job_id;
    }

    public void setJob_id(int job_id) {
        this.job_id = job_id;
    }

    public String getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(String completed_at) {
        this.completed_at = completed_at;
    }
}
