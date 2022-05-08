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
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.ArrayList;
import java.util.List;

@SuppressFBWarnings("EI_EXPOSE_REP2")
@Builder(toBuilder = true)
@PlanningSolution
public class TimeTable {
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
    HardSoftScore score;
    
    @Builder.Default
    @ConstraintConfigurationProvider
    RefereeConstraintConfiguration constraintConfiguration = new RefereeConstraintConfiguration();
    
    public TimeTable() {
    }
    
    public TimeTable(List<Game> games, List<Referee> referees, List<GameAssignment> gameAssignments, HardSoftScore score) {
        this.games = games;
        this.referees = referees;
        this.gameAssignments = gameAssignments;
        this.score = score;
    }
    
    public TimeTable(List<Game> games, List<Referee> referees, List<GameAssignment> gameAssignments, HardSoftScore score, RefereeConstraintConfiguration constraintConfiguration) {
        this.games = games;
        this.referees = referees;
        this.gameAssignments = gameAssignments;
        this.score = score;
        this.constraintConfiguration = constraintConfiguration;
    }
}
