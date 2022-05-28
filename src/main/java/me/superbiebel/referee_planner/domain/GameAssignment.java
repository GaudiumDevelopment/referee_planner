package me.superbiebel.referee_planner.domain;


import lombok.*;
import me.superbiebel.referee_planner.domain.comparators.RefereeStrengthComparator;
import me.superbiebel.referee_planner.pinningfilter.GameAssignmentPinningFilter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.UUID;


@Builder(toBuilder = true)
@PlanningEntity(pinningFilter = GameAssignmentPinningFilter.class)
@AllArgsConstructor
@NoArgsConstructor
@With
public class GameAssignment {
    @Getter
    @Setter
    private Game game;
    
    @PlanningId
    @Getter
    private UUID assignmentUUID;
    @Setter
    @Getter
    @PlanningVariable(valueRangeProviderRefs = "refereeList", strengthComparatorClass = RefereeStrengthComparator.class)
    private Referee referee;
    
    @Setter
    @Getter
    @Builder.Default
    private boolean pinnedReferee= false;
    @Getter
    private int indexInGame;
    
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
