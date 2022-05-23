package me.superbiebel.referee_planner.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.superbiebel.referee_planner.score.RefereeConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;

import java.util.ArrayList;
import java.util.List;


@Builder(toBuilder = true)
@PlanningSolution
public class RefereeTimeTable {
    
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
    
    public RefereeTimeTable() {
    }
    
    public RefereeTimeTable(List<Game> games, List<Referee> referees, List<GameAssignment> gameAssignments, HardMediumSoftLongScore score, RefereeConstraintConfiguration constraintConfiguration) {
        this.games = games;
        this.referees = referees;
        this.gameAssignments = gameAssignments;
        this.score = score;
        this.constraintConfiguration = constraintConfiguration;
    }
    
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
