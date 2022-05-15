package me.superbiebel.referee_planner.domain.comparators;

import me.superbiebel.referee_planner.domain.Referee;

import java.util.Comparator;

import static java.util.Comparator.comparing;

public class RefereeStrengthComparator implements Comparator<Referee> {
    
    private static final Comparator<Referee> REFEREE_STRENGTH_COMPARATOR = comparing(Referee::getExperience);
    
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
        return REFEREE_STRENGTH_COMPARATOR.compare(o1, o2);
    }
}
