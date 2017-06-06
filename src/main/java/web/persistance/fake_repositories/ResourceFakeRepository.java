package web.persistance.fake_repositories;

import org.springframework.data.repository.CrudRepository;
import web.persistance.fake_models.ResourceFake;

import java.util.List;

/**
 * Created by Miquel on 31/05/2017.
 */
public interface ResourceFakeRepository extends CrudRepository<ResourceFake,Integer> {
    List<ResourceFake> findAll();
}
