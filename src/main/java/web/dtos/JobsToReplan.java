package web.dtos;

import java.util.List;

/**
 * Created by Miquel on 31/05/2017.
 */
public class JobsToReplan {
    private List<CompletedJob> completedJobs;
    private List<InProgressJob> inProgressJobs;

    public JobsToReplan(){}

    public JobsToReplan(List<CompletedJob> completedJobs, List<InProgressJob> inProgressJobs) {
        this.completedJobs = completedJobs;
        this.inProgressJobs = inProgressJobs;
    }

    public List<CompletedJob> getCompletedJobs() {
        return completedJobs;
    }

    public void setCompletedJobs(List<CompletedJob> completedJobs) {
        this.completedJobs = completedJobs;
    }

    public List<InProgressJob> getInProgressJobs() {
        return inProgressJobs;
    }

    public void setInProgressJobs(List<InProgressJob> inProgressJobs) {
        this.inProgressJobs = inProgressJobs;
    }
}
