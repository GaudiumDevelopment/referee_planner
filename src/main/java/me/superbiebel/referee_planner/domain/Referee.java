package me.superbiebel.referee_planner.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
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
import java.util.stream.Collectors;

@SuppressFBWarnings("EI_EXPOSE_REP2")
@Builder(toBuilder = true)
@AllArgsConstructor
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
    @JsonIgnore
    @Setter
    private List<GameAssignment> assignments;
    
    @SuppressFBWarnings("EI_EXPOSE_REP")
    @Getter
    private List<Availability> availabilityList;
    
    public Referee() {
        //For optaplanner
    }
    
    @InverseRelationShadowVariable(sourceVariableName = "referee")
    public List<GameAssignment> getAssignments() {
        return Objects.requireNonNullElseGet(assignments, () -> (assignments = new ArrayList<>()));
    }
    
    public void addAssignment(GameAssignment assignment) {
        getAssignments().add(assignment);
    }
    
    public boolean checkIfAvailable(TimePeriod timePeriod) {
        return availabilityList.stream().anyMatch(availability -> availability.getTimePeriod().doesEncompass(timePeriod));
    }
    
    public Availability getCorrespondingAvailability(GameAssignment assignment) {
        List<Availability> foundAvailabilities = availabilityList.stream().filter(availability -> availability.getTimePeriod().doesEncompass(assignment.getGame().getGameRefereePeriod())).collect(Collectors.toList());
        if (foundAvailabilities.size() > 1) {
            throw new IllegalStateException("multiple availabilities found for gameAssigment: " + assignment.toString() + " in referee: " + this);
        }
        return foundAvailabilities.size() == 1 ? foundAvailabilities.get(0) : null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Referee referee = (Referee) o;
        
        if (getExperience() != referee.getExperience()) return false;
        if (isNonExist() != referee.isNonExist()) return false;
        if (!getRefereeUUID().equals(referee.getRefereeUUID())) return false;
        return getAvailabilityList().equals(referee.getAvailabilityList());
    }
    
    @Override
    public int hashCode() {
        int result = getRefereeUUID().hashCode();
        result = 31 * result + getExperience();
        result = 31 * result + (isNonExist() ? 1 : 0);
        result = 31 * result + getAvailabilityList().hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Referee{");
        sb.append("refereeUUID=").append(refereeUUID);
        sb.append(", isNonExist=").append(isNonExist);
        sb.append(", availabilityList=").append(availabilityList);
        sb.append('}');
        return sb.toString();
    }
}
