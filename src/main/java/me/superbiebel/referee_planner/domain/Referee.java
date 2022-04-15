package me.superbiebel.referee_planner.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.superbiebel.referee_planner.util.SortedArrayList;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

import java.util.UUID;

@Builder(toBuilder = true)
@PlanningEntity
public class Referee {
    @Getter
    private UUID uuid;
    @Getter
    private int experience;
    @Getter
    private boolean isNonExist = false;
    
    @Setter
    private SortedArrayList<GameAssignment> assignments;
    
    public Referee() {
    }
    
    public Referee(UUID uuid, int experience, boolean isNonExist, SortedArrayList<GameAssignment> assignments) {
        this.uuid = uuid;
        this.experience = experience;
        this.isNonExist = isNonExist;
        this.assignments = assignments;
    }
    
    @InverseRelationShadowVariable(sourceVariableName = "referee")
    public SortedArrayList<GameAssignment> getAssignments() {
        return assignments == null ? new SortedArrayList<>(new GameAssignmentComparator()):assignments;
    }
}
