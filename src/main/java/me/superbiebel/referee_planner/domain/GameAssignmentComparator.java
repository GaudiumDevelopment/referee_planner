package me.superbiebel.referee_planner.domain;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Comparator;

@SuppressFBWarnings("SE_COMPARATOR_SHOULD_BE_SERIALIZABLE")
public class GameAssignmentComparator implements Comparator<GameAssignment> {
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
