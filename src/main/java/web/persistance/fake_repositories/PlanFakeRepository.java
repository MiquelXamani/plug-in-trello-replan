package web.persistance.fake_repositories;

import org.springframework.data.repository.CrudRepository;
import web.persistance.fake_models.PlanFake;

/**
 * Created by Miquel on 31/05/2017.
 */
public interface PlanFakeRepository extends CrudRepository<PlanFake,Integer> {
}
