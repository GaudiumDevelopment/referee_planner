package me.superbiebel.referee_planner.problemchanges.referee;

import lombok.Builder;
import me.superbiebel.referee_planner.domain.Availability;
import me.superbiebel.referee_planner.domain.Referee;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import me.superbiebel.referee_planner.exceptions.LookupObjectNotFound;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.Collections;
import java.util.UUID;

@Builder(toBuilder = true)
public class RefereeAvailabilityChange implements ProblemChange<RefereeTimeTable> {
    private UUID refereeUUID;
    private Availability newAvailability;
    private UUID oldAvailabilityUUID;
    
    @Override
    public void doChange(RefereeTimeTable workingSolution, ProblemChangeDirector problemChangeDirector) throws LookupObjectNotFound {
        Referee referee = problemChangeDirector.lookUpWorkingObject(Referee.builder().refereeUUID(refereeUUID).build()).orElseThrow(LookupObjectNotFound::new);
        referee.removeRefereeFromGameAssignmentsByAvailability(oldAvailabilityUUID, problemChangeDirector);
        problemChangeDirector.changeProblemProperty(referee, referee1 -> referee1.getAvailabilityList()
                                                                                 .stream()
                                                                                 .filter(oldAvailability1 -> oldAvailability1.getAvailabilityUUID().equals(oldAvailabilityUUID))
                                                                                 .findFirst()
                                                                                 .ifPresent(oldAvailability1 -> {
                                                                                     referee1.getAvailabilityList().remove(oldAvailability1);
                                                                                     referee1.getAvailabilityToGameAssignmentsMap().remove(oldAvailability1);
                                                                                     referee1.getAvailabilityList().add(newAvailability);
                                                                                     referee1.getAvailabilityToGameAssignmentsMap().put(newAvailability, Collections.emptyList());
                                                                                 }));
        
    }
}
