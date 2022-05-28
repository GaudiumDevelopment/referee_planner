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
    @Setter
    @PlanningEntityCollectionProperty
    List<GameAssignment> gameAssignments;
    
    @Getter
    @Setter
    @Builder.Default
    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "refereeList")
    List<Referee> referees = new ArrayList<>();
    @Getter
    @Setter
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "gameList")
    private List<Game> games;
    
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
        
        if (!getRefereeTimeTableUUID().equals(that.getRefereeTimeTableUUID())) return false;
        if (!getGames().equals(that.getGames())) return false;
        if (!getReferees().equals(that.getReferees())) return false;
        if (!getGameAssignments().equals(that.getGameAssignments())) return false;
        if (!getScore().equals(that.getScore())) return false;
        return constraintConfiguration.equals(that.constraintConfiguration);
    }
    
    @Override
    public int hashCode() {
        int result = getRefereeTimeTableUUID().hashCode();
        result = 31 * result + getGames().hashCode();
        result = 31 * result + getReferees().hashCode();
        result = 31 * result + getGameAssignments().hashCode();
        result = 31 * result + getScore().hashCode();
        result = 31 * result + constraintConfiguration.hashCode();
        return result;
    }
}
