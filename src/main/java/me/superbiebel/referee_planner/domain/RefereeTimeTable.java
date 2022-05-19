package me.superbiebel.referee_planner.domain;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.Getter;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;

import java.util.ArrayList;
import java.util.List;

@SuppressFBWarnings("EI_EXPOSE_REP2")
@Builder(toBuilder = true)
@PlanningSolution
public class RefereeTimeTable {
    @SuppressFBWarnings("EI_EXPOSE_REP")
    @Getter
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "gameList")
    private List<Game> games;
    
    @SuppressFBWarnings("EI_EXPOSE_REP")
    @Getter
    @Builder.Default
    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "refereeList")
    List<Referee> referees = new ArrayList<>();
    
    @SuppressFBWarnings("EI_EXPOSE_REP")
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