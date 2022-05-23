package me.superbiebel.referee_planner.variablelisteners;

import me.superbiebel.referee_planner.domain.Referee;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import me.superbiebel.referee_planner.domain.comparators.GameAssignmentComparator;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

import java.util.ArrayList;

public class AssignmentSortVariableListener implements VariableListener<RefereeTimeTable, Referee> {
    
    public void updateList(ScoreDirector<RefereeTimeTable> scoreDirector, Referee referee) {
        if (referee.isRemoved()) return;
        scoreDirector.beforeVariableChanged(referee, "sortedAssignments");
        referee.setSortedAssignments(new ArrayList<>(referee.getAssignments()));
        referee.getSortedAssignments().sort(GameAssignmentComparator.COMPARATOR);
        scoreDirector.afterVariableChanged(referee, "sortedAssignments");
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
