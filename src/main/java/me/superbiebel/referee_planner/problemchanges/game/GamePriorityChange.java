package me.superbiebel.referee_planner.problemchanges.game;

import lombok.Builder;
import lombok.Getter;
import me.superbiebel.referee_planner.domain.Game;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.UUID;


public class GamePriorityChange extends GameProblemChange {
    @Getter
    private final int newPriority;
    @Builder(toBuilder = true)
    public GamePriorityChange(UUID gameUUID, int newPriority) {
        super(gameUUID);
        this.newPriority = newPriority;
    }
    
    @Override
    public void doChange(RefereeTimeTable workingSolution, ProblemChangeDirector problemChangeDirector) {
        Game game = Game.lookupGameByUUID(gameUUID, problemChangeDirector);
        problemChangeDirector.changeProblemProperty(game, game1 -> game1.setPriority(newPriority));
        game.removeRefereesFromGameAssignments(problemChangeDirector);
    }
    
    @Override
    public Game doActualChange(Game game, RefereeTimeTable workingSolution, ProblemChangeDirector problemChangeDirector) {
        return null;
    }
}
