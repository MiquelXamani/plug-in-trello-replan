package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.models.*;
import web.repositories.ResourceMemberRepository;
import web.repositories.UserRepository;
import web.services.ReplanService;
import web.services.TrelloService;

import java.util.*;

@RestController
@RequestMapping("/matchings")
public class MatchingsController {
    @Autowired(required = true)
    private UserRepository userRepository;
    @Autowired(required = true)
    private ResourceMemberRepository resourceMemberRepository;

    @RequestMapping(value = "/create-matchings", method = RequestMethod.POST)
    public ResponseEntity<Object> matchResourceWithMember(@RequestBody Matching[] matching){
        User u = userRepository.findByUsername(matchingDTO.getUsername());
        ResourceMember resourceMember = new ResourceMember(u.getUserId(),matchingDTO.getResourceId(),matchingDTO.getResourceName(),
                matchingDTO.getResourceDescription(),matchingDTO.getTrelloUserId(),matchingDTO.getTrelloUsername(),matchingDTO.getTrelloFullName());
        try {
            resourceMember = resourceMemberRepository.save(resourceMember);
            return new ResponseEntity<>(resourceMember, HttpStatus.CREATED);
        }
        catch (DataIntegrityViolationException e) {
            Map<String, String> error = new HashMap<>(1);
            error.put("description", "Already exists an association for this resource or team member");
            return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }
    }


    @RequestMapping(method = RequestMethod.GET)
    public MatchingDTO getMatchings(@RequestParam(value = "username") String username, @RequestParam(value = "projectId") String projectId,
                                    @RequestParam(value = "releaseId") String releaseId, @RequestParam(value = "teamId") String teamId){

        MatchingDTO matchingDTO = new MatchingDTO();
        ReplanService replanService = new ReplanService();

        //get plan
        Plan plan = replanService.getPlan(projectId,releaseId);
        matchingDTO.setPlan(plan);

        User user = userRepository.findByUsername(username);
        Long userId = user.getUserId();


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
        Resource r;
        Member m;
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
                        r = new Resource(resourceMember.getResourceId(),resourceMember.getResourceName(),resourceMember.getResourceDescription());
                        m = new Member(resourceMember.getTrelloUserId(),resourceMember.getTrelloUsername(),resourceMember.getTrelloFullName());
                        matching = new Matching(r,m);
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

        //sort unmatchet team members
        Collections.sort(unmatchedMembers);

        //create matchingDTO object
        matchingDTO.setMatchings(matchings);
        matchingDTO.setUnmatchedResources(unmatchedResources);
        matchingDTO.setUnmatchedMembers(unmatchedMembers);

        return matchingDTO;
    }


}
