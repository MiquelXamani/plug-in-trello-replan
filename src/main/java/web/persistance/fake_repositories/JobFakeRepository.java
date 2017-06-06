package web.persistance.fake_repositories;

import org.springframework.data.repository.CrudRepository;
import web.persistance.fake_models.JobFake;

/**
 * Created by Miquel on 31/05/2017.
 */
public interface JobFakeRepository extends CrudRepository<JobFake,String> {
}
