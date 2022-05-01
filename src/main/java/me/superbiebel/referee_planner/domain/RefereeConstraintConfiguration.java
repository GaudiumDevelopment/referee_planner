package me.superbiebel.referee_planner.domain;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@ConstraintConfiguration
public class RefereeConstraintConfiguration {
    public static final String SUFFICIENT_HARD_MINIMUM_EXPERIENCE_LEVEL = "SUFFICIENT_HARD_MINIMUM_EXPERIENCE_LEVEL";
    public static final String SUFFICIENT_SOFT_MINIMUM_EXPERIENCE_LEVEL = "SUFFICIENT_SOFT_MINIMUM_EXPERIENCE_LEVEL";
    public static final String SUFFICIENT_SOFT_MAXIMUM_EXPERIENCE_LEVEL = "SUFFICIENT_SOFT_MAXIMUM_EXPERIENCE_LEVEL";
    public static final String DISTANCE_SOFT = "DISTANCE_SOFT";
    public static final String NOT_ENOUGH_REFEREES = "NOT_ENOUGH_REFEREES";
    public static final String TOO_MUCH_REFEREES = "TOO MUCH REFEREES";
    public static final String FIRST_REFEREE_IS_NOT_NON_EXIST = "FIRST REFEREE IS NON EXIST";
    public static final String FIRST_REFEREE_MORE_EXP_THEN_OTHER = "FIRST REFEREE MORE EXP THEN OTHER";
    
    @ConstraintWeight(SUFFICIENT_HARD_MINIMUM_EXPERIENCE_LEVEL)
    HardSoftScore hardMinimumExperience = HardSoftScore.ofHard(1);
    
    @ConstraintWeight(SUFFICIENT_SOFT_MINIMUM_EXPERIENCE_LEVEL)
    HardSoftScore softMinimumExperience = HardSoftScore.ofSoft(1);
    
    @ConstraintWeight(SUFFICIENT_SOFT_MAXIMUM_EXPERIENCE_LEVEL)
    HardSoftScore softMaximumExperienceLevel = HardSoftScore.ofSoft(1);
    
    @ConstraintWeight(DISTANCE_SOFT)
    HardSoftScore distanceSoft = HardSoftScore.ofSoft(1);
    
    @ConstraintWeight(NOT_ENOUGH_REFEREES)
    HardSoftScore notEnoughReferees = HardSoftScore.ofSoft(1);
    @ConstraintWeight(TOO_MUCH_REFEREES)
    HardSoftScore tooMuchReferees = HardSoftScore.ofSoft(1);
    
    @ConstraintWeight(FIRST_REFEREE_IS_NOT_NON_EXIST)
    HardSoftScore firstReferenceIsNonExist = HardSoftScore.ofHard(1);
    
    @ConstraintWeight(FIRST_REFEREE_MORE_EXP_THEN_OTHER)
    HardSoftScore firstRefereeMoreExpThanOther = HardSoftScore.ofSoft(1);
    
    
}
