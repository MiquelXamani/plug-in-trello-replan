package web.persistance.repositories;

import org.springframework.data.repository.CrudRepository;
import web.persistance.models.CardPersist;
import web.persistance.models.JobPersist;

import java.util.List;

/**
 * Created by Miquel on 16/05/2017.
 */
public interface JobRepository extends CrudRepository<JobPersist,String> {
    JobPersist findFirstByFeatureId(int featureId);
    JobPersist findFirstByFeatureIdAndBoardEndpointIdAndBoardReleaseIdAndBoardProjectId(int featureId, int endpointId, int releaseId, int projectId);
    List<JobPersist> findByCardIdIn(List<String> cardsIds);
    JobPersist findFirstByJobIdAndAndBoardEndpointIdAndBoardReleaseIdAndBoardProjectId(int jobId,int endpointId,int releaseId, int projectId);
}
