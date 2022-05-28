package me.superbiebel.referee_planner.domain;

import lombok.*;
import me.superbiebel.referee_planner.score.RefereeConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@PlanningSolution
public class RefereeTimeTable {
    @Getter
    @PlanningId
    private UUID refereeTimeTableUUID;
    
    @Getter
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "gameList")
    private List<Game> games;
    
    @Getter
    @Setter
    @Builder.Default
    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "refereeList")
    List<Referee> referees = new ArrayList<>();
    
    @Getter
    @PlanningEntityCollectionProperty
    List<GameAssignment> gameAssignments;
    
    @Getter
    @PlanningScore
    HardMediumSoftLongScore score;
    
    @Builder.Default
    @ConstraintConfigurationProvider
    RefereeConstraintConfiguration constraintConfiguration = new RefereeConstraintConfiguration();
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        RefereeTimeTable that = (RefereeTimeTable) o;
        
        if (!getGames().equals(that.getGames())) return false;
        if (!getReferees().equals(that.getReferees())) return false;
        return getGameAssignments().equals(that.getGameAssignments());
    }
    
    @Override
    public int hashCode() {
        int result = getGames().hashCode();
        result = 31 * result + getReferees().hashCode();
        result = 31 * result + getGameAssignments().hashCode();
        return result;
    }
}
