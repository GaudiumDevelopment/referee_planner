package me.superbiebel.referee_planner.domain;


import lombok.Builder;
import lombok.Getter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@Builder(toBuilder = true)
@PlanningEntity
public class GameAssignment {
    @Getter
    private Game game;
    @Getter
    @PlanningVariable(valueRangeProviderRefs = "refereeList")
    private Referee referee;
    @Getter
    private int indexInGame;
    
    public GameAssignment(Game game, int indexInGame) {
        this.game = game;
        this.indexInGame = indexInGame;
    }
    
    public GameAssignment() {
    }
    
    public GameAssignment(Game game, Referee referee, int indexInGame) {
        this.game = game;
        this.referee = referee;
        this.indexInGame = indexInGame;
    }
}
