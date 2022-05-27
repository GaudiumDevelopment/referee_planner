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
        Referee referee = Referee.lookupRefereeByUUID(refereeUUID, problemChangeDirector);
        referee.removeAvailability(oldAvailabilityUUID, problemChangeDirector);
        referee.addAvailability(newAvailability, problemChangeDirector);
    }
}
