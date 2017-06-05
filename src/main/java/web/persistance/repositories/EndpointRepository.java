package web.persistance.repositories;

import org.springframework.data.repository.CrudRepository;
import web.persistance.models.EndpointPersist;

import java.util.List;

public interface EndpointRepository extends CrudRepository<EndpointPersist, Integer> {
    List<EndpointPersist> findAll();
}
