package me.superbiebel.referee_planner.problemchanges.game;

import lombok.Builder;
import lombok.Getter;
import me.superbiebel.referee_planner.domain.Game;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.UUID;

@Builder(toBuilder = true)
public class GamePriorityChange implements ProblemChange<RefereeTimeTable> {
    @Getter
    private UUID gameUUID;
    @Getter
    private int newPriority;
    
    @Override
    public void doChange(RefereeTimeTable workingSolution, ProblemChangeDirector problemChangeDirector) {
        Game game = Game.lookupGameByUUID(gameUUID, problemChangeDirector);
        problemChangeDirector.changeProblemProperty(game, game1 -> game1.setPriority(newPriority));
        game.removeRefereesFromGameAssignments(problemChangeDirector);
    }
}
