package me.superbiebel.referee_planner.domain;


import lombok.Builder;
import lombok.Getter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.UUID;
@Builder(toBuilder = true)
@PlanningEntity
public class GameAssignment {
    @Getter
    private UUID gameUUID;
    @Getter
    private Game game;
    @Getter
    @PlanningVariable(valueRangeProviderRefs = "refereeList")
    private Referee referee;
    @Getter
    private int indexInGame;
    
    public GameAssignment(UUID gameUUID, Game game, int indexInGame) {
        this.gameUUID = gameUUID;
        this.game = game;
        this.indexInGame = indexInGame;
    }
    
    public GameAssignment() {
    }
    
    public GameAssignment(UUID gameUUID, Game game, Referee referee, int indexInGame) {
        this.gameUUID = gameUUID;
        this.game = game;
        this.referee = referee;
        this.indexInGame = indexInGame;
    }
}
