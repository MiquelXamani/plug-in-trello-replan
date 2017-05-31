package web.persistance.models.fake_models;

import javax.persistence.*;

@Entity
public class JobPrecedence {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "previousJobId")
    private JobFake previousJob;
    @ManyToOne
    @JoinColumn(name = "nextJobId")
    private JobFake nextJob;

    public JobPrecedence(JobFake previousJob, JobFake nextJob) {
        this.previousJob = previousJob;
        this.nextJob = nextJob;
        this.id = previousJob.getId() + "-" + nextJob.getId();
    }

    public JobFake getPreviousJob() {
        return previousJob;
    }

    public void setPreviousJob(JobFake previousJob) {
        this.previousJob = previousJob;
    }

    public JobFake getNextJob() {
        return nextJob;
    }

    public void setNextJob(JobFake nextJob) {
        this.nextJob = nextJob;
    }
}
