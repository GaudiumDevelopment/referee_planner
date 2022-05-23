package me.superbiebel.referee_planner.problemchanges.referee;

import lombok.Builder;
import me.superbiebel.referee_planner.domain.Availability;
import me.superbiebel.referee_planner.domain.Referee;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import me.superbiebel.referee_planner.exceptions.LookupObjectNotFound;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.UUID;

@Builder(toBuilder = true)
public class RefereeAvailabilityChange implements ProblemChange<RefereeTimeTable> {
    private UUID refereeUUID;
    private Availability newAvailability;
    private UUID oldAvailabilityUUID;
    
    @Override
    public void doChange(RefereeTimeTable workingSolution, ProblemChangeDirector problemChangeDirector) throws LookupObjectNotFound {
        Referee referee = problemChangeDirector.lookUpWorkingObject(Referee.builder().refereeUUID(refereeUUID).build()).orElseThrow(LookupObjectNotFound::new);
        problemChangeDirector.changeProblemProperty(referee, referee1 -> referee1.getAvailabilityList()
                                                                                 .stream()
                                                                                 .filter(availability -> availability.getAvailabilityUUID().equals(oldAvailabilityUUID))
                                                                                 .findFirst()
                                                                                 .ifPresent(availability -> {
                                                                                     referee1.getAvailabilityList().remove(availability);
                                                                                     referee1.getAvailabilityList().add(newAvailability);
                                                                                 }));
    }
}
