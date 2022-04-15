package me.superbiebel.referee_planner;

import me.superbiebel.referee_planner.domain.GameAssignement;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

public class RefereeConstaintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                sufficientHardMinimumExperienceLevel(constraintFactory),
                sufficientSoftMinimumExperienceLevel(constraintFactory),
                sufficientSoftMaximumExperienceLevel(constraintFactory)
        };
    }
    Constraint sufficientHardMinimumExperienceLevel(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(GameAssignement.class)
                       .filter(gameAssignment -> !gameAssignment.getReferee().isNonExist())
                       .filter(gameAssignment -> gameAssignment.getGame().getHardMinimumExperience() > gameAssignment.getReferee().getExperience())
                       .penalize("Not enough exp (hard)", HardSoftScore.ONE_HARD, gameAssignment -> gameAssignment.getGame().getHardMinimumExperience() - gameAssignment.getReferee().getExperience());
    }
    Constraint sufficientSoftMinimumExperienceLevel(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(GameAssignement.class)
                       .filter(gameAssignment -> gameAssignment.getGame().getSoftMinimumExperience() > gameAssignment.getReferee().getExperience())
                       .penalize("Not enough exp (soft)", HardSoftScore.ONE_SOFT, gameAssignment -> gameAssignment.getGame().getSoftMinimumExperience() - gameAssignment.getReferee().getExperience());
    }
    Constraint sufficientSoftMaximumExperienceLevel(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(GameAssignement.class)
                       .filter(gameAssignment -> gameAssignment.getGame().getSoftMaximumExperience() > gameAssignment.getReferee().getExperience())
                       .penalize("Too much exp", HardSoftScore.ONE_SOFT, gameAssignment -> gameAssignment.getReferee().getExperience() - gameAssignment.getGame().getSoftMaximumExperience());
    }
}
