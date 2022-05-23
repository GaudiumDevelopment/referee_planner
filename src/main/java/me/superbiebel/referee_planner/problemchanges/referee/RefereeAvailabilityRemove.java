package me.superbiebel.referee_planner.problemchanges.referee;

import lombok.Builder;
import lombok.Getter;
import me.superbiebel.referee_planner.domain.Availability;
import me.superbiebel.referee_planner.domain.Referee;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import me.superbiebel.referee_planner.exceptions.LookupObjectNotFound;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.UUID;
@Builder(toBuilder = true)
public class RefereeAvailabilityRemove implements ProblemChange<RefereeTimeTable> {
    @Getter
    private UUID refereeUUID;
    @Getter
    private UUID oldAvailabilityUUID;
    
    @Override
    public void doChange(RefereeTimeTable workingSolution, ProblemChangeDirector problemChangeDirector) {
        Referee referee = problemChangeDirector.lookUpWorkingObject(Referee.builder().refereeUUID(refereeUUID).build()).orElseThrow(LookupObjectNotFound::new);
        Availability foundAvailability = referee.getAvailabilityToGameAssignmentsMap().keySet().stream().filter(availability -> availability.getAvailabilityUUID().equals(oldAvailabilityUUID)).findFirst().orElseThrow(LookupObjectNotFound::new);
        referee.removeRefereeFromGameAssignmentsByAvailability(oldAvailabilityUUID, problemChangeDirector);
        problemChangeDirector.changeProblemProperty(referee, referee1 -> referee1.getAvailabilityList().remove(foundAvailability));
        problemChangeDirector.changeProblemProperty(referee, referee1 -> referee1.getAvailabilityToGameAssignmentsMap().remove(foundAvailability));
    }
}
