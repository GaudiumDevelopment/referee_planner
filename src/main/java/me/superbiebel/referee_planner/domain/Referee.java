package me.superbiebel.referee_planner.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.superbiebel.referee_planner.exceptions.LookupObjectNotFound;
import me.superbiebel.referee_planner.variablelisteners.AssignmentSortVariableListener;
import me.superbiebel.referee_planner.variablelisteners.AvailabilityAssignmentMapVariableListener;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.*;
import java.util.stream.Collectors;

@Builder(toBuilder = true)
@AllArgsConstructor
@PlanningEntity
public class Referee {
    @PlanningId
    @Getter
    private UUID refereeUUID;
    @Getter
    @Setter
    private int experience;
    @Getter
    @Builder.Default
    private boolean isNonExist = false;
    @JsonIgnore
    @Setter
    private List<GameAssignment> assignments;
    
    @Getter
    private List<Availability> availabilityList;
    
    //calculated
    @JsonIgnore
    @Setter
    private List<GameAssignment> sortedAssignments;
    @Setter
    private Map<Availability, List<GameAssignment>> availabilityToGameAssignmentsMap;
    @Setter
    private List<GameAssignment> unassignedAssignments;
    
    public Referee() {
        //For optaplanner
    }
    public void removeRefereeFromGameAssignmentsByAvailability(UUID availabilityUUID, ProblemChangeDirector problemChangeDirector) {
        Availability foundAvailability = availabilityToGameAssignmentsMap.keySet().stream().filter(availability -> availability.getAvailabilityUUID().equals(availabilityUUID)).findFirst().orElse(null);
        if (foundAvailability == null) return; //the availability was not found inside the map because it had no gameAssignments coupled to it
        List<GameAssignment> gameAssignmentList = Objects.requireNonNull(availabilityToGameAssignmentsMap.get(foundAvailability));
        gameAssignmentList.forEach(gameAssignment -> problemChangeDirector.changeVariable(gameAssignment, "referee", gameAssignment1 -> gameAssignment1.setReferee(null)));
    }
    public static Referee lookupRefereeByUUID(UUID refereeUUID, ProblemChangeDirector problemChangeDirector) {
        return problemChangeDirector.lookUpWorkingObject(Referee.builder().refereeUUID(refereeUUID).build()).orElseThrow(LookupObjectNotFound::new);
    }
    
    @InverseRelationShadowVariable(sourceVariableName = "referee")
    public List<GameAssignment> getAssignments() {
        return Objects.requireNonNullElseGet(assignments, () -> (assignments = new ArrayList<>()));
    }
    
    @CustomShadowVariable(sources = @PlanningVariableReference(variableName = "assignments"), variableListenerClass = AssignmentSortVariableListener.class)
    public List<GameAssignment> getSortedAssignments() {
        return Objects.requireNonNullElseGet(sortedAssignments, () -> (sortedAssignments = new ArrayList<>()));
    }
    
    @CustomShadowVariable(sources = {@PlanningVariableReference(variableName = "assignments"), @PlanningVariableReference(variableName = "sortedAssignments")}, variableListenerClass = AvailabilityAssignmentMapVariableListener.class)
    public Map<Availability, List<GameAssignment>> getAvailabilityToGameAssignmentsMap() {
        return Objects.requireNonNullElseGet(availabilityToGameAssignmentsMap, () -> (availabilityToGameAssignmentsMap = new HashMap<>()));
    }
    
    @CustomShadowVariable(variableListenerRef = @PlanningVariableReference(variableName = "availabilityToGameAssignmentsMap"))
    public List<GameAssignment> getUnassignedAssignments() {
        return Objects.requireNonNullElseGet(unassignedAssignments, () -> (unassignedAssignments = new ArrayList<>()));
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
