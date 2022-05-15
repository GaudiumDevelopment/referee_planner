package me.superbiebel.referee_planner.domain.comparators;

import me.superbiebel.referee_planner.domain.Referee;

import java.util.Collections;
import java.util.Comparator;

import static java.util.Comparator.comparing;

public class RefereeDifficultyComparator implements Comparator<Referee> {
    
    private static final Comparator<Referee> REFEREE_DIFFICULTY_COMPARATOR = comparing(Referee::getExperience, Collections.reverseOrder(Integer::compareTo));
    
    @Override
    public int compare(Referee o1, Referee o2) {
        if (o1 == null) {
            if (o2 == null) {
                return 0;
            }
            return -1;
        } else if (o2 == null) {
            return 1;
        }
        return REFEREE_DIFFICULTY_COMPARATOR.compare(o1, o2);
    }
}
