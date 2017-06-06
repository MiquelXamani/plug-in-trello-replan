package web.persistance.fake_repositories;

import org.springframework.data.repository.CrudRepository;
import web.persistance.fake_models.FeatureFake;

import java.util.List;

/**
 * Created by Miquel on 31/05/2017.
 */
public interface FeatureFakeRepository extends CrudRepository<FeatureFake,Integer> {
    List<FeatureFake> findAll();
}
