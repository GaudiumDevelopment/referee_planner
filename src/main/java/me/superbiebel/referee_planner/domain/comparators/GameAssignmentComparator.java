package me.superbiebel.referee_planner.domain.comparators;

import me.superbiebel.referee_planner.domain.GameAssignment;

import java.util.Comparator;

public class GameAssignmentComparator implements Comparator<GameAssignment> {
    
    private static final Comparator<GameAssignment> INNER = Comparator.comparing(gameAssignment -> gameAssignment.getGame().getGamePeriod().getStart());
    
    public static final Comparator<GameAssignment> COMPARATOR = INNER.thenComparingInt(GameAssignment::getIndexInGame);
    
    public GameAssignmentComparator() {
        //for optaplanner
    }
    
    /**
     * Earlier has lower index in sorted list
     */
    @Override
    public int compare(GameAssignment o1, GameAssignment o2) {
        if (o1 == null) {
            if (o2 == null) {
                return 0;
            }
            return -1;
        } else if (o2 == null) {
            return 1;
        }
        return COMPARATOR.compare(o1, o2);
    }
}
