package me.superbiebel.referee_planner;

import io.quarkus.logging.Log;
import me.superbiebel.referee_planner.domain.*;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

import java.util.ArrayList;

public class RefereeConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                sufficientHardMinimumExperienceLevel(constraintFactory),
                sufficientSoftMinimumExperienceLevel(constraintFactory),
                sufficientSoftMaximumExperienceLevel(constraintFactory),
                distanceSoftConstraint(constraintFactory),
                NotEnoughRefereesConstraint(constraintFactory),
                TooManyRefereesConstraint(constraintFactory)
        };
    }
    public Constraint sufficientHardMinimumExperienceLevel(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(GameAssignment.class)
                       .filter(gameAssignment -> !gameAssignment.getReferee().isNonExist())
                       .filter(gameAssignment -> gameAssignment.getGame().getHardMinimumExperience() > gameAssignment.getReferee().getExperience())
                       .penalize("Not enough exp (hard)", RefereeConstraintConfiguration.hardMinimumExperience, gameAssignment -> gameAssignment.getGame().getHardMinimumExperience() - gameAssignment.getReferee().getExperience());
    }
    public Constraint sufficientSoftMinimumExperienceLevel(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(GameAssignment.class)
                       .filter(gameAssignment -> !gameAssignment.getReferee().isNonExist())
                       .filter(gameAssignment -> gameAssignment.getGame().getSoftMinimumExperience() > gameAssignment.getReferee().getExperience())
                       .penalize("Not enough exp (soft)", RefereeConstraintConfiguration.softMinimumExperience, gameAssignment -> gameAssignment.getGame().getSoftMinimumExperience() - gameAssignment.getReferee().getExperience());
    }
    public Constraint sufficientSoftMaximumExperienceLevel(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(GameAssignment.class)
                       .filter(gameAssignment -> !gameAssignment.getReferee().isNonExist())
                       .filter(gameAssignment -> gameAssignment.getGame().getSoftMaximumExperience() < gameAssignment.getReferee().getExperience())
                       .penalize("Too much exp (soft)", RefereeConstraintConfiguration.softMaximumExperienceLevel, gameAssignment -> gameAssignment.getReferee().getExperience() - gameAssignment.getGame().getSoftMaximumExperience());
    }
    public Constraint distanceSoftConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Referee.class)
                       .filter(referee -> !referee.isNonExist())
                       .penalize("Distance soft", RefereeConstraintConfiguration.distanceSoft, referee -> {
                           ArrayList<GameAssignment> gameAssignments = new ArrayList<>(referee.getAssignments());
                           gameAssignments.sort(new GameAssignmentComparator());
                           
                           
                           if (gameAssignments.isEmpty()) {
                               return 0;
                           }
                           
                           long amount = 0;
                           GameAssignment lastAssignment = null;
                           Log.debug("assignment size: " + gameAssignments.size());
                           amount += referee.getHomeLocation().getDistanceTo(gameAssignments.get(0).getGame().getGameLocation());
                           
                           
                           for (GameAssignment assignment : gameAssignments) {
                               Log.debug("assignment location: " + assignment.getGame().getGameLocation());
                           }
                           
                           for (GameAssignment assignment : gameAssignments) {
                               if (lastAssignment != null) {
                                   Log.debug("last assignment location: " + lastAssignment.getGame().getGameLocation().toString());
                                   Log.debug("current assignment location: " + assignment.getGame().getGameLocation().toString());
                                   
                                   
                                   long addingAmount = lastAssignment.getGame().getGameLocation().getDistanceTo(assignment.getGame().getGameLocation());
                                   
                                   
                                   Log.debug("old amount: " + amount);
                                   Log.debug("amount to be added: " + addingAmount);
                                   amount += addingAmount;
                                   Log.debug("new amount: " + amount);
                               } else {
                                   Log.debug("Last assignment was null.");
                               }
                               lastAssignment = assignment;
                           }
                           return (int)amount;
                       });
    }
    
    public Constraint NotEnoughRefereesConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Game.class)
                .filter(game -> game.getAssignments().stream().filter(gameAssignment -> gameAssignment.getReferee() != null)
                    .filter(gameAssignment -> !gameAssignment.getReferee().isNonExist()).count() < game.getAmountOfRefereesNeeded())
                .penalize("refereeCount", RefereeConstraintConfiguration.notEnoughReferees);
    }
    public Constraint TooManyRefereesConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Game.class)
                       .filter(game -> game.getAssignments().stream().filter(gameAssignment -> gameAssignment.getReferee() != null)
                           .filter(gameAssignment -> !gameAssignment.getReferee().isNonExist()).count() > game.getAmountOfRefereesNeeded())
                       .penalize("refereeCount", RefereeConstraintConfiguration.tooMuchReferees);
    }
}
