package me.superbiebel.referee_planner.problemchanges.game;

import lombok.Builder;
import lombok.Getter;
import me.superbiebel.referee_planner.domain.Game;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import me.superbiebel.referee_planner.domain.TimePeriod;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.UUID;


public class GamePeriodChange extends GameProblemChange {
    @Getter
    private final TimePeriod newPeriod;
    @Builder(toBuilder = true)
    public GamePeriodChange(UUID gameUUID, TimePeriod newPeriod) {
        super(gameUUID);
        this.newPeriod = newPeriod;
    }
    
    @Override
    public Game doActualChange(Game game, RefereeTimeTable workingSolution, ProblemChangeDirector problemChangeDirector) {
        return game.withGamePeriod(newPeriod).withGameRefereePeriod(newPeriod.toBuilder().start(newPeriod.getStart().minusMinutes(20)).build());
    }
}
