package web.repositories;

import org.springframework.data.repository.CrudRepository;
import web.models.ResourceMember;

import java.util.List;

public interface ResourceMemberRepository extends CrudRepository<ResourceMember, Long> {
    List<ResourceMember> findByUserIdAndResourceIdIn(String userId, List<String> resourceIds);
    List<ResourceMember> findByUserIdAndTrelloUserIdIn(String userId, List<String> trelloUserIds);
}
