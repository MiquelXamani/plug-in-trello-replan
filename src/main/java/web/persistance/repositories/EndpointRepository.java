package web.persistance.repositories;

import org.springframework.data.repository.CrudRepository;
import web.persistance.models.Endpoint;

import java.util.List;

public interface EndpointRepository extends CrudRepository<Endpoint, Integer> {
    List<Endpoint> findAll();
}
