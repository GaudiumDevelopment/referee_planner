package me.superbiebel.referee_planner.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressFBWarnings("EI_EXPOSE_REP2")
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
    private List<GameAssignment> assignments;
    
    @SuppressFBWarnings("EI_EXPOSE_REP")
    @Getter
    private List<TimePeriod> availabilityList;
    
    public Referee() {
        //For optaplanner
    }
    
    public Referee(UUID uuid, int experience, boolean isNonExist, Location homeLocation, List<GameAssignment> assignments, List<TimePeriod> availabilityList) {
        this.uuid = uuid;
        this.experience = experience;
        this.isNonExist = isNonExist;
        this.homeLocation = homeLocation;
        this.assignments = assignments;
        this.availabilityList = availabilityList;
    }
    
    @InverseRelationShadowVariable(sourceVariableName = "referee")
    public List<GameAssignment> getAssignments() {
        return Objects.requireNonNullElseGet(assignments, () -> (assignments = new ArrayList<>()));
    }
    
    public void addAssignment(GameAssignment assignment) {
        getAssignments().add(assignment);
    }
}
