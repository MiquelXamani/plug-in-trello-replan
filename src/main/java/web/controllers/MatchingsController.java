package web.controllers;

import web.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.persistance.repositories.ResourceMemberRepository;
import web.persistance.models.ResourceMember;
import web.domain_controllers.DomainController;
import web.services.ReplanFake;
import web.services.ReplanService;
import web.services.TrelloService;

import java.util.*;

@RestController
@RequestMapping("/matchings")
public class MatchingsController {
    @Autowired(required = true)
    private ResourceMemberRepository resourceMemberRepository;
    private DomainController domainController;

    @Autowired
    public MatchingsController(DomainController domainController){
        this.domainController = domainController;
    }

    @RequestMapping(value = "/create-matchings", method = RequestMethod.POST)
    public ResponseEntity<Object> createMatchings(@RequestParam(value = "username") String username, @RequestBody Matching[] newMatchings){
        Resource r;
        Member m;
        ResourceMember resourceMember;
        User2 u = domainController.getUser(username);
        Long userId = u.getUserId();
        List <ResourceMember> resourceMembers = new ArrayList<>();
        for (Matching matching: newMatchings) {
            r = matching.getResource();
            m = matching.getMember();
            try {
                resourceMember = new ResourceMember(userId,r.getId(),r.getName(),r.getDescription(),m.getId(),
                        m.getUsername(),m.getFullName());
                resourceMember = resourceMemberRepository.save(resourceMember);

            }
            catch (DataIntegrityViolationException e){
                resourceMember = resourceMemberRepository.findByUserIdAndResourceId(userId,r.getId());
                resourceMember.setResourceId(r.getId());
                resourceMember.setResourceName(r.getName());
                resourceMember.setResourceDescription(r.getDescription());
                resourceMember.setTrelloUserId(m.getId());
                resourceMember.setTrelloUsername(m.getUsername());
                resourceMember.setTrelloFullName(m.getFullName());
                resourceMemberRepository.save(resourceMember);
            }
            resourceMembers.add(resourceMember);
        }
        return new ResponseEntity<>(resourceMembers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/delete-matchings", method = RequestMethod.POST)
    public List<ResourceMember> deleteMatchings(@RequestParam(value = "username") String username, @RequestBody Matching[] matchingsToDelete){
        User2 u = domainController.getUser(username);
        Long userId = u.getUserId();
        List <ResourceMember> resourceMembersDeleted = new ArrayList<>();
        ResourceMember resourceMember;
        for (Matching matching: matchingsToDelete) {
            resourceMember = resourceMemberRepository.findByUserIdAndResourceIdAndTrelloUserId(userId,matching.getResource().getId(),matching.getMember().getId());
            if(resourceMember != null){
                resourceMemberRepository.delete(resourceMember);
                resourceMembersDeleted.add(resourceMember);
            }
        }

        return resourceMembersDeleted;
    }

    public Matching createMatchingObject(ResourceMember resourceMember){
        Resource resource = new Resource(resourceMember.getResourceId(),resourceMember.getResourceName(),resourceMember.getResourceDescription());
        Member member = new Member(resourceMember.getTrelloUserId(),resourceMember.getTrelloUsername(),resourceMember.getTrelloFullName());
        Matching matching = new Matching(resource,member);
        return matching;
    }

    @RequestMapping(method = RequestMethod.GET)
    public MatchingsContainer getMatchings(@RequestParam(value = "username") String username, @RequestParam(value = "endpointId") String endpointId,
                                    @RequestParam(value = "projectId") String projectId,
                                    @RequestParam(value = "releaseId") String releaseId, @RequestParam(value = "teamId") String teamId){

        MatchingsContainer matchingDTO = new MatchingsContainer();
        ReplanService replanService = new ReplanService();
        ReplanFake replanFake = new ReplanFake(domainController);

        //get plan
        String url = domainController.getEndpoint(Integer.parseInt(endpointId)).getUrl();
        Plan plan;
        if(url.equals("simulation mode")){
            plan = replanFake.getPlan(projectId,releaseId);
            System.out.println("PLAN FROM JSON");
        }
        else {
            System.out.println("PLAN FROM ENDPOINT URL " + url);
            plan = replanService.getPlan(url, projectId, releaseId);
        }
        matchingDTO.setPlan(plan);

        User2 user = domainController.getUser(username);
        Long userId = user.getUserId();
        System.out.println("****************" + userId.toString());


        //get plan resources without repetitions
        List<Integer> resourcesIds = new ArrayList<>();
        Map<Integer,Resource> resources = new HashMap<>();
        List<Job> jobs = plan.getJobs();
        for (Job j:jobs) {
            boolean found = false;
            int i = 0;
            int rId = j.getResource().getId();
            while(!found && i < resourcesIds.size()){
                if(rId == resourcesIds.get(i)) {
                    found = true;
                }
                i++;
            }
            if(!found){
                resourcesIds.add(rId);
                resources.put(rId,j.getResource());
            }
        }

        //get matchings
        Collections.sort(resourcesIds);
        List<ResourceMember> resourcesFound = resourceMemberRepository.findByUserIdAndResourceIdInOrderByResourceId(userId,resourcesIds);

        //separate resources matched and not matched
        List<Resource> unmatchedResources = new ArrayList<>();
        List<Matching> matchings = new ArrayList<>();
        int resId;
        ResourceMember resourceMember;
        Matching matching;
        boolean found;
        if(resourcesIds.size() != resourcesFound.size()){
            int j = 0;
            for(int i = 0; i < resourcesFound.size(); i++){
                found = false;
                while(!found){
                    resourceMember = resourcesFound.get(i);
                    if(resourcesIds.get(j) == resourceMember.getResourceId()){
                        found = true;
                        matching = createMatchingObject(resourceMember);
                        matchings.add(matching);
                    }
                    else{
                        resId = resourcesIds.get(j);
                        unmatchedResources.add(resources.get(resId));
                    }
                    j++;
                }

            }
            for(int k = j; k < resourcesIds.size(); k++){
                resId = resourcesIds.get(k);
                unmatchedResources.add(resources.get(resId));
            }

        }
        //all plan resources are already associated team member
        else{
            for (ResourceMember rm2: resourcesFound) {
                matching = createMatchingObject(rm2);
                matchings.add(matching);
            }
        }

        TrelloService trelloService = new TrelloService();
        String trelloToken = user.getTrelloToken();
        TeamWithMembers teamWithMembers = trelloService.getTrelloTeamMembers(teamId,trelloToken);
        List<Member> members = teamWithMembers.getMembers();
        //member list is sorted in order to make easier finding unmatched team members
        Collections.sort(members);

        List<String> trelloUsernames = new ArrayList<>();
        for(int i = 0; i < members.size(); i++){
            trelloUsernames.add(members.get(i).getUsername());
        }

        List<ResourceMember> foundMembers = resourceMemberRepository.findByUserIdAndTrelloUsernameInOrderByTrelloUsernameAsc(userId,trelloUsernames);

        List<Member> unmatchedMembers = new ArrayList<>();

        //recórrer la llista inicial per trobar aquells membres que apareguin a la base de dades
        ResourceMember rm;
        Member m;
        if(members.size() != foundMembers.size()) {
            int j = 0;
            for (int i = 0; i < foundMembers.size(); i++) {
                rm = foundMembers.get(i);
                found = false;
                while (!found) {
                    m = members.get(j);
                    if (rm.getTrelloUsername().equals(m.getUsername())) {
                        found = true;
                    } else {
                        unmatchedMembers.add(m);
                    }
                    j++;
                }
            }

            for (int k = j; k < members.size(); k++) {
                unmatchedMembers.add(members.get(k));
            }
        }


        //sort matching list

        //sort unmatched resources list
        Collections.sort(unmatchedResources);

        //sort unmatched team members
        Collections.sort(unmatchedMembers);

        //create matchingDTO object
        matchingDTO.setMatchings(matchings);
        matchingDTO.setUnmatchedResources(unmatchedResources);
        matchingDTO.setUnmatchedMembers(unmatchedMembers);

        return matchingDTO;
    }
}