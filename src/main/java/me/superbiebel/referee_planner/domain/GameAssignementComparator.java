package me.superbiebel.referee_planner.domain;

import java.util.Comparator;

public class GameAssignementComparator implements Comparator<GameAssignement> {
    @Override
    public int compare(GameAssignement o1, GameAssignement o2) {
        if (o1.getGamePeriod().getStart().isBefore(o2.getGamePeriod().getStart())) {
            return 1;
        } else if (o1.getGamePeriod().getStart().isAfter(o2.getGamePeriod().getStart())) {
            return -1;
        } else if (o1.getGamePeriod().getStart().isEqual(o2.getGamePeriod().getStart())) {
            return 0;
        } else {
            throw new IllegalStateException("could not compare two game periods" + o1.getGamePeriod().getStart() + o2.getGamePeriod().getStart());
        }
    }
}
