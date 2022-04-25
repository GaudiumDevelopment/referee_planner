package me.superbiebel.referee_planner.domain;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

@ConstraintConfiguration
public class RefereeConstraintConfiguration {
    public static final String SUFFICIENT_HARD_MINIMUM_EXPERIENCE_LEVEL = "SUFFICIENT_HARD_MINIMUM_EXPERIENCE_LEVEL";
    public static final String SUFFICIENT_SOFT_MINIMUM_EXPERIENCE_LEVEL = "SUFFICIENT_SOFT_MINIMUM_EXPERIENCE_LEVEL";
    public static final String SUFFICIENT_SOFT_MAXIMUM_EXPERIENCE_LEVEL = "SUFFICIENT_SOFT_MAXIMUM_EXPERIENCE_LEVEL";
    public static final String DISTANCE_SOFT = "DISTANCE_SOFT";
    public static final String NOT_ENOUGH_REFEREES = "NOT_ENOUGH_REFEREES";
    private static final String TOO_MUCH_REFEREES = "TOO MUCH REFEREES";
    
    @ConstraintWeight(SUFFICIENT_HARD_MINIMUM_EXPERIENCE_LEVEL)
    public static final HardSoftLongScore hardMinimumExperience = HardSoftLongScore.ofHard(1);
    
    @ConstraintWeight(SUFFICIENT_SOFT_MINIMUM_EXPERIENCE_LEVEL)
    public static final HardSoftLongScore softMinimumExperience = HardSoftLongScore.ofSoft(1);
    
    @ConstraintWeight(SUFFICIENT_SOFT_MAXIMUM_EXPERIENCE_LEVEL)
    public static final HardSoftLongScore softMaximumExperienceLevel = HardSoftLongScore.ofSoft(1);
    
    @ConstraintWeight(DISTANCE_SOFT)
    public static final HardSoftLongScore distanceSoft = HardSoftLongScore.ofSoft(1);
    
    @ConstraintWeight(NOT_ENOUGH_REFEREES)
    public static final HardSoftLongScore notEnoughReferees = HardSoftLongScore.ofSoft(1);
    @ConstraintWeight(TOO_MUCH_REFEREES)
    public static final HardSoftLongScore tooMuchReferees = HardSoftLongScore.ofSoft(1);
}
