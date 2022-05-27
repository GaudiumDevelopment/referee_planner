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
import java.util.concurrent.ConcurrentHashMap;
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
    private boolean removed;
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
    public Availability getAvailabilityByUUID(UUID availabilityUUID) {
        return availabilityList.stream().filter(availability -> availability.getAvailabilityUUID().equals(availabilityUUID)).findFirst().orElse(null);
    }
    
    public void addAvailability(Availability availability, ProblemChangeDirector problemChangeDirector) {
        problemChangeDirector.changeProblemProperty(this, referee1 -> referee1.getAvailabilityList().add(availability));
    }
    public void addAvailability(Availability availability) {
        this.getAvailabilityList().add(availability);
    }
    public void removeAvailability(Availability availability, ProblemChangeDirector problemChangeDirector) {
        List<GameAssignment> gameAssignmentList = availabilityToGameAssignmentsMap.get(availability);
        if (gameAssignmentList != null) {
            gameAssignmentList.forEach(gameAssignment -> removeRefereeFromGameAssignment(gameAssignment, problemChangeDirector));
        }
        problemChangeDirector.changeProblemProperty(this, referee1 -> referee1.getAvailabilityList().remove(availability));
        problemChangeDirector.changeProblemProperty(this, referee1 -> referee1.getAvailabilityToGameAssignmentsMap().remove(availability));
    }
    public void removeAvailability(UUID availabilityUUID, ProblemChangeDirector problemChangeDirector) {
        removeAvailability(getAvailabilityByUUID(availabilityUUID), problemChangeDirector);
    }
    public void removeAllAvailability(ProblemChangeDirector problemChangeDirector) {
        availabilityToGameAssignmentsMap.values().forEach(gameAssignments -> gameAssignments.forEach(gameAssignment -> this.removeRefereeFromGameAssignment(gameAssignment, problemChangeDirector)));
        availabilityToGameAssignmentsMap.clear();
    }
    public void removeRefereeFromGameAssignment(GameAssignment gameAssignment, ProblemChangeDirector problemChangeDirector) {
        availabilityToGameAssignmentsMap.forEach((availability, assignmentList) -> assignmentList.forEach(assignment -> {
            if (gameAssignment.equals(assignment)) {
                problemChangeDirector.changeVariable(gameAssignment, "referee", gameAssignment1 -> gameAssignment1.setReferee(null));
                problemChangeDirector.changeProblemProperty(this, referee1 -> {
                    referee1.getSortedAssignments().remove(gameAssignment);
                    referee1.getUnassignedAssignments().remove(gameAssignment);
                    referee1.getAssignments().remove(gameAssignment);
                });
            }
        }));
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
        return Objects.requireNonNullElseGet(availabilityToGameAssignmentsMap, () -> (availabilityToGameAssignmentsMap = new ConcurrentHashMap<>()));
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
