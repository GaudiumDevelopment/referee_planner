package me.superbiebel.referee_planner.problemchanges.referee;

import lombok.Builder;
import lombok.Getter;
import me.superbiebel.referee_planner.domain.Referee;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public class RemoveRefereeChange implements ProblemChange<RefereeTimeTable> {
    @Getter
    private UUID refereeUUID;
    @Override
    public void doChange(RefereeTimeTable workingSolution, ProblemChangeDirector problemChangeDirector) {
        Referee referee = Referee.lookupRefereeByUUID(refereeUUID, problemChangeDirector);
        problemChangeDirector.changeProblemProperty(referee, referee1 -> referee1.setRemoved(true));
        referee.removeAllAvailability(problemChangeDirector);
    
        List<Referee> refereeList = new ArrayList<>(workingSolution.getReferees());
        workingSolution.setReferees(refereeList);
        problemChangeDirector.removeEntity(referee, referee1 -> workingSolution.getReferees().remove(referee1));
    }
}
