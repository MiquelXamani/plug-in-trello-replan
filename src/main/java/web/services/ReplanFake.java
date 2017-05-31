package web.services;

import web.domain.JobsToReplan;
import web.domain.UpdatedPlan;

public class ReplanFake {
    public ReplanFake(){}

    public UpdatedPlan doReplanFake(String url, int projectId, int releaseId, JobsToReplan jobsToReplan){

        return new UpdatedPlan();
    }
}
