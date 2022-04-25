package me.superbiebel.referee_planner.domain;

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

@Builder(toBuilder = true)
@PlanningSolution
public class TimeTable {
    @Getter
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "gameList")
    private List<Game> games;
    
    @Getter
    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "refereeList")
    List<Referee> referees = new ArrayList<>();
    
    @Getter
    @PlanningEntityCollectionProperty
    List<GameAssignment> gameAssignments;
    
    @Getter
    @PlanningScore
    HardSoftScore score;
    
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
}
