package me.superbiebel.referee_planner.problemchanges.referee;

import lombok.Builder;
import lombok.Getter;
import me.superbiebel.referee_planner.domain.Referee;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
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
        Referee referee = Referee.lookupRefereeByUUID(refereeUUID, problemChangeDirector);
        referee.removeAvailability(oldAvailabilityUUID, problemChangeDirector);
    }
}
