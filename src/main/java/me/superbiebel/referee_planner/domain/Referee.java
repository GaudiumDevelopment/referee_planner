package me.superbiebel.referee_planner.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressFBWarnings("EI_EXPOSE_REP2")
@Builder(toBuilder = true)
@PlanningEntity
public class Referee {
    @PlanningId
    @Getter
    private UUID refereeUUID;
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
    
    public Referee(UUID refereeUUID, int experience, boolean isNonExist, Location homeLocation, List<GameAssignment> assignments, List<TimePeriod> availabilityList) {
        this.refereeUUID = refereeUUID;
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
    
    public boolean checkIfAvailable(TimePeriod timePeriod) {
        return availabilityList.stream().anyMatch(availability -> availability.doesEncompass(timePeriod));
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Referee{");
        sb.append("refereeUUID=").append(refereeUUID);
        sb.append('}');
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Referee referee = (Referee) o;
        
        if (getExperience() != referee.getExperience()) return false;
        if (isNonExist() != referee.isNonExist()) return false;
        if (!getRefereeUUID().equals(referee.getRefereeUUID())) return false;
        if (!getHomeLocation().equals(referee.getHomeLocation())) return false;
        return getAvailabilityList().equals(referee.getAvailabilityList());
    }
    
    @Override
    public int hashCode() {
        int result = getRefereeUUID().hashCode();
        result = 31 * result + getExperience();
        result = 31 * result + (isNonExist() ? 1 : 0);
        result = 31 * result + getHomeLocation().hashCode();
        result = 31 * result + getAvailabilityList().hashCode();
        return result;
    }
}
