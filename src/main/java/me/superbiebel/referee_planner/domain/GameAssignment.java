package me.superbiebel.referee_planner.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.superbiebel.referee_planner.domain.comparators.RefereeStrengthComparator;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.UUID;


@Builder(toBuilder = true)
@PlanningEntity
public class GameAssignment {
    @Getter
    private Game game;
    
    @PlanningId
    @Getter
    private UUID assignmentUUID;
    @Setter
    @Getter
    @PlanningVariable(valueRangeProviderRefs = "refereeList", strengthComparatorClass = RefereeStrengthComparator.class)
    private Referee referee;
    @Getter
    private int indexInGame;
    
    public GameAssignment() {
    }
    
    public GameAssignment(Game game, UUID assignmentUUID, Referee referee, int indexInGame) {
        this.game = game;
        this.assignmentUUID = assignmentUUID;
        this.referee = referee;
        this.indexInGame = indexInGame;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GameAssignment{");
        sb.append("game=").append(game);
        sb.append(", assignmentUUID=").append(assignmentUUID);
        sb.append(", indexInGame=").append(indexInGame);
        sb.append('}');
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        GameAssignment that = (GameAssignment) o;
        
        if (getIndexInGame() != that.getIndexInGame()) return false;
        return getGame().equals(that.getGame());
    }
    
    @Override
    public int hashCode() {
        int result = getGame().hashCode();
        result = 31 * result + getIndexInGame();
        return result;
    }
}
