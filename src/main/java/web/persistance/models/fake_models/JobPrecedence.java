package web.persistance.models.fake_models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class JobPrecedence {
    @Id
    private String id;
    @OneToMany(mappedBy = "jobprecedence")
    private JobFake previousJob;
    @OneToMany(mappedBy = "jobprecedence")
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
