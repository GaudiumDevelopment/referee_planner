package me.superbiebel.referee_planner.problemchanges.gameassignment;

import lombok.Getter;
import me.superbiebel.referee_planner.domain.Referee;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.UUID;

public class PinRefereeToGameAssignmentChange implements ProblemChange<RefereeTimeTable> {
    @Getter
    private Referee pinnedReferee;
    @Getter
    private UUID g
    
    @Override
    public void doChange(RefereeTimeTable workingSolution, ProblemChangeDirector problemChangeDirector) {
        problemChangeDirector.lookUpWorkingObject(Referee.builder().refereeUUID(p))
        problemChangeDirector.changeVariable();
    }
}
