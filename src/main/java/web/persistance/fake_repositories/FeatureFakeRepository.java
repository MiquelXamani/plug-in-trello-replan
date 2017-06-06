package web.persistance.fake_repositories;

import org.springframework.data.repository.CrudRepository;
import web.persistance.fake_models.FeatureFake;

/**
 * Created by Miquel on 31/05/2017.
 */
public interface FeatureFakeRepository extends CrudRepository<FeatureFake,String> {
}
