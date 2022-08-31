package me.superbiebel.referee_planner.score;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;

@ConstraintConfiguration
public class RefereeConstraintConfiguration {
    public static final String SUFFICIENT_HARD_MINIMUM_EXPERIENCE_LEVEL = "SUFFICIENT_HARD_MINIMUM_EXPERIENCE_LEVEL";
    public static final String SUFFICIENT_SOFT_MINIMUM_EXPERIENCE_LEVEL = "SUFFICIENT_SOFT_MINIMUM_EXPERIENCE_LEVEL";
    public static final String SUFFICIENT_SOFT_MAXIMUM_EXPERIENCE_LEVEL = "SUFFICIENT_SOFT_MAXIMUM_EXPERIENCE_LEVEL";
    public static final String DISTANCE_SOFT = "DISTANCE_SOFT";
    public static final String NOT_ENOUGH_REFEREES = "NOT_ENOUGH_REFEREES";
    public static final String TOO_MUCH_REFEREES = "TOO MUCH REFEREES";
    public static final String NONEXIST_REF_NOT_ON_LOWER_INDEX_EXIST_REFEREE = "FIRST REFEREE IS NON EXIST";
    public static final String FIRST_REFEREE_MORE_EXP_THEN_OTHER = "FIRST REFEREE MORE EXP THEN OTHER";
    public static final String SAME_REFEREE_MULTIPLE_GAME_INDEX = "SAME REFEREE MULTIPLE GAME INDEX";
    public static final String IS_PHYSICALLY_POSSIBLE = "IS PHYSICALLY POSSIBLE";
    public static final String GAMEASSIGNMENT_DOES_NOT_OVERLAP_PER_REFEREE = "GAMEASSIGNMENT DOES NOT OVERLAP";
    public static final String MAX_RANGE = "MAX RANGE NOT VIOLATED";
    
    
    @ConstraintWeight(SUFFICIENT_HARD_MINIMUM_EXPERIENCE_LEVEL)
    HardMediumSoftLongScore hardMinimumExperience = HardMediumSoftLongScore.ofHard(1);
    
    @ConstraintWeight(SUFFICIENT_SOFT_MINIMUM_EXPERIENCE_LEVEL)
    HardMediumSoftLongScore softMinimumExperience = HardMediumSoftLongScore.ofSoft(20_000);
    
    @ConstraintWeight(SUFFICIENT_SOFT_MAXIMUM_EXPERIENCE_LEVEL)
    HardMediumSoftLongScore softMaximumExperienceLevel = HardMediumSoftLongScore.ofSoft(20_000);
    
    @ConstraintWeight(DISTANCE_SOFT)
    HardMediumSoftLongScore distanceSoft = HardMediumSoftLongScore.ofSoft(1);
    
    @ConstraintWeight(NOT_ENOUGH_REFEREES)
    HardMediumSoftLongScore notEnoughReferees = HardMediumSoftLongScore.ofMedium(1);
    @ConstraintWeight(TOO_MUCH_REFEREES)
    HardMediumSoftLongScore tooMuchReferees = HardMediumSoftLongScore.ofSoft(20_000);
    
    @ConstraintWeight(NONEXIST_REF_NOT_ON_LOWER_INDEX_EXIST_REFEREE)
    HardMediumSoftLongScore firstReferenceIsNonExist = HardMediumSoftLongScore.ofHard(1);
    
    @ConstraintWeight(FIRST_REFEREE_MORE_EXP_THEN_OTHER)
    HardMediumSoftLongScore firstRefereeMoreExpThanOther = HardMediumSoftLongScore.ofSoft(20_000);
    
    @ConstraintWeight(SAME_REFEREE_MULTIPLE_GAME_INDEX)
    HardMediumSoftLongScore sameRefereeMultipleGameIndex = HardMediumSoftLongScore.ofHard(1);
    
    @ConstraintWeight(IS_PHYSICALLY_POSSIBLE)
    HardMediumSoftLongScore isInAvailabilityConstraint = HardMediumSoftLongScore.ofHard(1);
    
    @ConstraintWeight(GAMEASSIGNMENT_DOES_NOT_OVERLAP_PER_REFEREE)
    HardMediumSoftLongScore gameAssignmentDoesNotOverlapPerReferee = HardMediumSoftLongScore.ofHard(1);
    
    @ConstraintWeight(MAX_RANGE)
    HardMediumSoftLongScore maxRange = HardMediumSoftLongScore.ofHard(1);
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        RefereeConstraintConfiguration that = (RefereeConstraintConfiguration) o;
        
        if (!hardMinimumExperience.equals(that.hardMinimumExperience)) return false;
        if (!softMinimumExperience.equals(that.softMinimumExperience)) return false;
        if (!softMaximumExperienceLevel.equals(that.softMaximumExperienceLevel)) return false;
        if (!distanceSoft.equals(that.distanceSoft)) return false;
        if (!notEnoughReferees.equals(that.notEnoughReferees)) return false;
        if (!tooMuchReferees.equals(that.tooMuchReferees)) return false;
        if (!firstReferenceIsNonExist.equals(that.firstReferenceIsNonExist)) return false;
        if (!firstRefereeMoreExpThanOther.equals(that.firstRefereeMoreExpThanOther)) return false;
        if (!sameRefereeMultipleGameIndex.equals(that.sameRefereeMultipleGameIndex)) return false;
        if (!isInAvailabilityConstraint.equals(that.isInAvailabilityConstraint)) return false;
        if (!gameAssignmentDoesNotOverlapPerReferee.equals(that.gameAssignmentDoesNotOverlapPerReferee)) return false;
        return maxRange.equals(that.maxRange);
    }
    
    @Override
    public int hashCode() {
        int result = hardMinimumExperience.hashCode();
        result = 31 * result + softMinimumExperience.hashCode();
        result = 31 * result + softMaximumExperienceLevel.hashCode();
        result = 31 * result + distanceSoft.hashCode();
        result = 31 * result + notEnoughReferees.hashCode();
        result = 31 * result + tooMuchReferees.hashCode();
        result = 31 * result + firstReferenceIsNonExist.hashCode();
        result = 31 * result + firstRefereeMoreExpThanOther.hashCode();
        result = 31 * result + sameRefereeMultipleGameIndex.hashCode();
        result = 31 * result + isInAvailabilityConstraint.hashCode();
        result = 31 * result + gameAssignmentDoesNotOverlapPerReferee.hashCode();
        result = 31 * result + maxRange.hashCode();
        return result;
    }
}
