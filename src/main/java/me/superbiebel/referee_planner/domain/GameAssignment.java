package me.superbiebel.referee_planner.domain;


import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.Getter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@SuppressFBWarnings("EI_EXPOSE_REP2")
@Builder(toBuilder = true)
@PlanningEntity
public class GameAssignment {
    @SuppressFBWarnings("EI_EXPOSE_REP")
    @Getter
    private Game game;
    @SuppressFBWarnings("EI_EXPOSE_REP")
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
