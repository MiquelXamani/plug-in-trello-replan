package web.persistance.repositories;

import org.springframework.data.repository.CrudRepository;
import web.persistance.models.CardPersist;
import web.persistance.models.JobPersist;

/**
 * Created by Miquel on 16/05/2017.
 */
public interface JobRepository extends CrudRepository<JobPersist,String> {
    JobPersist findFirstByFeatureId(int featureId);
}
