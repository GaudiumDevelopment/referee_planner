package me.superbiebel.referee_planner.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@Builder(toBuilder = true)
@PlanningEntity
public class Referee {
    @Getter
    private UUID uuid;
    @Getter
    private int experience;
    @Getter
    @Builder.Default
    private boolean isNonExist = false;
    @Getter
    private Location homeLocation;
    @JsonIgnore
    @Setter
    private ArrayList<GameAssignment> assignments;
    
    public Referee() {
    }
    
    public Referee(UUID uuid, int experience, boolean isNonExist, Location homeLocation, ArrayList<GameAssignment> assignments) {
        this.uuid = uuid;
        this.experience = experience;
        this.isNonExist = isNonExist;
        this.homeLocation = homeLocation;
        this.assignments = assignments;
    }
    
    @InverseRelationShadowVariable(sourceVariableName = "referee")
    public ArrayList<GameAssignment> getAssignments() {
        return Objects.requireNonNullElseGet(assignments, () -> (assignments = new ArrayList<>()));
    }
    public void addAssignment(GameAssignment assignment) {
        getAssignments().add(assignment);
    }
}
