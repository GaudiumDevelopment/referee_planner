package me.superbiebel.referee_planner.domain;

import java.util.Comparator;

public class GameAssignmentComparator implements Comparator<GameAssignment> {
    public GameAssignmentComparator() {
        //for optaplanner
    }
    
    /**
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return 1 if o1 is earlier, 0 if equal, -1 if o1 is later
     */
    @Override
    public int compare(GameAssignment o1, GameAssignment o2) {
        if (o1.getGame().getGamePeriod().getStart().isBefore(o2.getGame().getGamePeriod().getStart())) {
            return 1;
        } else if (o1.getGame().getGamePeriod().getStart().isAfter(o2.getGame().getGamePeriod().getStart())) {
            return -1;
        } else if (o1.getGame().getGamePeriod().getStart().isEqual(o2.getGame().getGamePeriod().getStart())) {
            return 0;
        } else {
            throw new IllegalStateException("could not compare two game periods" + o1.getGame().getGamePeriod().getStart() + o2.getGame().getGamePeriod().getStart());
        }
    }
}
