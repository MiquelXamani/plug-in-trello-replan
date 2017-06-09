package web.persistance.repositories;

import org.springframework.data.repository.CrudRepository;
import web.domain.Card;
import web.persistance.models.CardPersist;

import java.util.List;

/**
 * Created by Miquel on 16/05/2017.
 */
public interface CardRepository extends CrudRepository<CardPersist,String> {
    CardPersist findFirstByLogsId(int logId);
    List<CardPersist> findByBoardIdAndAcceptedTrue(String boardId);
    CardPersist findFirstByBoardEndpointIdAndBoardProjectIdAndBoardReleaseIdAndJobsJobId(int endpointId, int projectId, int releaseId, int jobId);
    CardPersist findFirstByBoardEndpointIdAndBoardProjectIdAndBoardReleaseIdAndJobsFeatureId(int endpointId, int projectId, int releaseId, int featureId);
}
