package web.repositories;

import org.springframework.data.repository.CrudRepository;
import web.models.ResourceMember;

import java.util.List;

public interface ResourceMemberRepository extends CrudRepository<ResourceMember, Long> {
    List<ResourceMember> findByUserIdAndResourceIdInOrderByResourceId(Long userId, List<Integer> resourceIds);
    ResourceMember findByUserIdAndResourceId(Long userId, int resourceId);
    ResourceMember findByUserIdAndResourceIdAndTrelloUserId(Long userId, int resourceId, String memberId);

    //fer orderby per algun atribut, ha de ser el mateix que a TeamsController
    List<ResourceMember> findByUserIdAndTrelloUsernameInOrderByTrelloUsernameAsc(Long userId, List<String> trelloUsernames);
}
