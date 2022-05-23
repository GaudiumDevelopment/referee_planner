package me.superbiebel.referee_planner.problemchanges.game;

import lombok.Builder;
import lombok.Getter;
import me.superbiebel.referee_planner.domain.Game;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import me.superbiebel.referee_planner.domain.TimePeriod;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.UUID;

@Builder(toBuilder = true)
public class GamePeriodChange implements ProblemChange<RefereeTimeTable> {
    @Getter
    private UUID gameUUID;
    @Getter
    private TimePeriod newPeriod;
    
    @Override
    public void doChange(RefereeTimeTable workingSolution, ProblemChangeDirector problemChangeDirector) {
        Game game = Game.lookupGameByUUID(gameUUID, problemChangeDirector);
        problemChangeDirector.changeProblemProperty(game, game1 -> {
            game1.setGamePeriod(newPeriod);
            game1.setGameRefereePeriod(newPeriod.toBuilder().start(newPeriod.getStart().minusMinutes(20)).build());
        });
        game.removeRefereesFromGameAssignments(problemChangeDirector);
    }
}
