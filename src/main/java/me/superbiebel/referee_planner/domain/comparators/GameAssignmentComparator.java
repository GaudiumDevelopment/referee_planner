package me.superbiebel.referee_planner.domain.comparators;

import me.superbiebel.referee_planner.domain.GameAssignment;

import java.util.Comparator;

public class GameAssignmentComparator implements Comparator<GameAssignment> {
    
    public static final GameAssignmentComparator COMPARATOR = new GameAssignmentComparator();
    
    public GameAssignmentComparator() {
        //for optaplanner
    }
    
    /**
     * Earlier has lower index in sorted list
     */
    @Override
    public int compare(GameAssignment o1, GameAssignment o2) {
        if (o1.getGame().getGameRefereePeriod().getStart().isBefore(o2.getGame().getGameRefereePeriod().getStart())) {
            return -1;
        } else if (o1.getGame().getGameRefereePeriod().getStart().isAfter(o2.getGame().getGameRefereePeriod().getStart())) {
            return 1;
        } else if (o1.getGame().getGameRefereePeriod().getStart().isEqual(o2.getGame().getGameRefereePeriod().getStart())) {
            return 0;
        } else {
            throw new IllegalStateException("could not compare two game periods" + o1.getGame().getGameRefereePeriod().getStart() + o2.getGame().getGameRefereePeriod().getStart());
        }
    }
}
