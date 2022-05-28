package me.superbiebel.referee_planner.problemchanges.game;

import lombok.Builder;
import lombok.Getter;
import me.superbiebel.referee_planner.domain.Game;
import me.superbiebel.referee_planner.domain.Location;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.UUID;


public class GameLocationChange extends GameProblemChange {
    @Getter
    private final Location newLocation;
    @Builder(toBuilder = true)
    public GameLocationChange(UUID gameUUID, Location newLocation) {
        super(gameUUID);
        this.newLocation = newLocation;
    }
    
    @Override
    public Game doActualChange(Game game, RefereeTimeTable workingSolution, ProblemChangeDirector problemChangeDirector) {
        return game.withGameLocation(newLocation);
    }
}
