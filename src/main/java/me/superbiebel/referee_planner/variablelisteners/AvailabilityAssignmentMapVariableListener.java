package me.superbiebel.referee_planner.variablelisteners;

import me.superbiebel.referee_planner.domain.Availability;
import me.superbiebel.referee_planner.domain.GameAssignment;
import me.superbiebel.referee_planner.domain.Referee;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import me.superbiebel.referee_planner.domain.comparators.GameAssignmentComparator;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvailabilityAssignmentMapVariableListener implements VariableListener<RefereeTimeTable, Referee> {
    
    public void updateList(ScoreDirector<RefereeTimeTable> scoreDirector, Referee referee) {
        Map<Availability, List<GameAssignment>> availabilityGameAssignmentMap = new HashMap<>();
        List<GameAssignment> unassignedAssignments = new ArrayList<>();
        for (GameAssignment gameAssignment : referee.getAssignments()) {
            Availability availability;
            List<GameAssignment> assignmentList;
            if ((availability = referee.getCorrespondingAvailability(gameAssignment)) != null) {
                if ((assignmentList = availabilityGameAssignmentMap.get(availability)) == null) {
                    assignmentList = new ArrayList<>();
                    assignmentList.add(gameAssignment);
                    availabilityGameAssignmentMap.put(availability, assignmentList);
                } else {
                    assignmentList.add(gameAssignment);
                }
            } else {
                unassignedAssignments.add(gameAssignment);
            }
        }
        for (Map.Entry<Availability, List<GameAssignment>> entry : availabilityGameAssignmentMap.entrySet()) {
            List<GameAssignment> assignmentList = entry.getValue();
            assignmentList.sort(GameAssignmentComparator.COMPARATOR);
        }
        scoreDirector.beforeVariableChanged(referee, "availabilityToGameAssignmentsMap");
        referee.setAvailabilityToGameAssignmentsMap(availabilityGameAssignmentMap);
        scoreDirector.afterVariableChanged(referee, "availabilityToGameAssignmentsMap");
        scoreDirector.beforeVariableChanged(referee, "unassignedAssignments");
        referee.setUnassignedAssignments(unassignedAssignments);
        scoreDirector.afterVariableChanged(referee, "unassignedAssignments");
    }
    
    @Override
    public void beforeEntityAdded(ScoreDirector<RefereeTimeTable> scoreDirector, Referee referee) {
        //nothing needed
    }
    
    @Override
    public void afterEntityAdded(ScoreDirector<RefereeTimeTable> scoreDirector, Referee referee) {
        updateList(scoreDirector, referee);
    }
    
    @Override
    public void beforeVariableChanged(ScoreDirector<RefereeTimeTable> scoreDirector, Referee referee) {
        //nothing needed
    }
    
    @Override
    public void afterVariableChanged(ScoreDirector<RefereeTimeTable> scoreDirector, Referee referee) {
        updateList(scoreDirector, referee);
    }
    
    @Override
    public void beforeEntityRemoved(ScoreDirector<RefereeTimeTable> scoreDirector, Referee referee) {
        //nothing needed
    }
    
    @Override
    public void afterEntityRemoved(ScoreDirector<RefereeTimeTable> scoreDirector, Referee referee) {
        updateList(scoreDirector, referee);
    }
}
