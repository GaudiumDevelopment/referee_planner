package me.superbiebel.referee_planner.problemchanges.referee;

import lombok.Builder;
import lombok.Getter;
import me.superbiebel.referee_planner.domain.Availability;
import me.superbiebel.referee_planner.domain.GameAssignment;
import me.superbiebel.referee_planner.domain.Referee;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import me.superbiebel.referee_planner.exceptions.LookupObjectNotFound;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
@Builder(toBuilder = true)
public class RefereeAvailabilityRemove implements ProblemChange<RefereeTimeTable> {
    @Getter
    private UUID refereeUUID;
    @Getter
    private UUID availabilityUUID;
    
    @Override
    public void doChange(RefereeTimeTable workingSolution, ProblemChangeDirector problemChangeDirector) {
        Referee referee = problemChangeDirector.lookUpWorkingObject(Referee.builder().refereeUUID(refereeUUID).build()).orElseThrow(LookupObjectNotFound::new);
        Availability foundAvailability = referee.getAvailabilityToGameAssignmentsMap().keySet().stream().filter(availability -> availability.getAvailabilityUUID().equals(availabilityUUID)).findFirst().orElseThrow(LookupObjectNotFound::new);
        List<GameAssignment> gameAssignmentList = Objects.requireNonNull(referee.getAvailabilityToGameAssignmentsMap().get(foundAvailability));
        gameAssignmentList.forEach(gameAssignment -> problemChangeDirector.changeVariable(gameAssignment, "referee", gameAssignment1 -> gameAssignment1.setReferee(null)));
        problemChangeDirector.changeProblemProperty(referee, referee1 -> referee1.getAvailabilityList().remove(foundAvailability));
        problemChangeDirector.changeProblemProperty(referee, referee1 -> referee1.getAvailabilityToGameAssignmentsMap().remove(foundAvailability));
    }
}
