package me.superbiebel.referee_planner.problemchanges.gameassignment;

import lombok.Builder;
import lombok.Getter;
import me.superbiebel.referee_planner.domain.GameAssignment;
import me.superbiebel.referee_planner.domain.Referee;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.UUID;

@Builder(toBuilder = true)
public class PinRefereeToGameAssignmentChange implements ProblemChange<RefereeTimeTable> {
    @Getter
    private UUID refereeUUID;
    @Getter
    private UUID assignmentUUID;
    
    @Override
    public void doChange(RefereeTimeTable workingSolution, ProblemChangeDirector problemChangeDirector) {
        Referee referee = problemChangeDirector.lookUpWorkingObject(Referee.builder().refereeUUID(refereeUUID).build()).orElseThrow();
        GameAssignment gameAssignment = problemChangeDirector.lookUpWorkingObject(GameAssignment.builder().assignmentUUID(assignmentUUID).build()).orElseThrow();
        problemChangeDirector.changeVariable(gameAssignment, "referee", gameAssignment1 -> gameAssignment1.setReferee(referee));
        problemChangeDirector.changeProblemProperty(referee, referee1 -> referee1.addAssignment(gameAssignment));
    }
}
