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
                notEnoughRefereesConstraint(constraintFactory),
                tooManyRefereesConstraint(constraintFactory),
                firstRefereeIsNotNonExistingConstraint(constraintFactory),
                sameRefereeMultipleGameIndexConstraint(constraintFactory),
                isInAvailabilityConstraint(constraintFactory)
        };
    }
    
    public Constraint sufficientHardMinimumExperienceLevel(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(GameAssignment.class)
                       .filter(gameAssignment -> !gameAssignment.getReferee().isNonExist())
                       .filter(gameAssignment -> gameAssignment.getGame().getHardMinimumExperience() > gameAssignment.getReferee().getExperience())
                       .penalizeConfigurable(RefereeConstraintConfiguration.SUFFICIENT_HARD_MINIMUM_EXPERIENCE_LEVEL, gameAssignment -> gameAssignment.getGame().getHardMinimumExperience() - gameAssignment.getReferee().getExperience());
    }
    public Constraint sufficientSoftMinimumExperienceLevel(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(GameAssignment.class)
                       .filter(gameAssignment -> !gameAssignment.getReferee().isNonExist())
                       .filter(gameAssignment -> gameAssignment.getGame().getSoftMinimumExperience() > gameAssignment.getReferee().getExperience())
                       .penalizeConfigurable(RefereeConstraintConfiguration.SUFFICIENT_SOFT_MINIMUM_EXPERIENCE_LEVEL, gameAssignment -> gameAssignment.getGame().getSoftMinimumExperience() - gameAssignment.getReferee().getExperience());
    }
    public Constraint sufficientSoftMaximumExperienceLevel(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(GameAssignment.class)
                       .filter(gameAssignment -> !gameAssignment.getReferee().isNonExist())
                       .filter(gameAssignment -> gameAssignment.getGame().getSoftMaximumExperience() < gameAssignment.getReferee().getExperience())
                       .penalizeConfigurable(RefereeConstraintConfiguration.SUFFICIENT_SOFT_MAXIMUM_EXPERIENCE_LEVEL, gameAssignment -> gameAssignment.getReferee().getExperience() - gameAssignment.getGame().getSoftMaximumExperience());
    }
    public Constraint distanceSoftConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Referee.class)
                       .filter(referee -> !referee.isNonExist())
                       .penalizeConfigurable(RefereeConstraintConfiguration.DISTANCE_SOFT, referee -> {
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
    
    public Constraint notEnoughRefereesConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Game.class)
                .filter(game -> game.getAssignments().stream().filter(gameAssignment -> gameAssignment.getReferee() != null)
                    .filter(gameAssignment -> !gameAssignment.getReferee().isNonExist()).count() < game.getAmountOfRefereesNeeded())
                       .penalizeConfigurable(RefereeConstraintConfiguration.NOT_ENOUGH_REFEREES);
    }
    public Constraint tooManyRefereesConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Game.class)
                       .filter(game -> game.getAssignments()
                                               .stream()
                                               .filter(gameAssignment -> gameAssignment.getReferee() != null)
                                               .filter(gameAssignment -> !gameAssignment.getReferee().isNonExist()).count() > game.getAmountOfRefereesNeeded())
                       .penalizeConfigurable(RefereeConstraintConfiguration.TOO_MUCH_REFEREES);
    }
    
    public Constraint firstRefereeIsNotNonExistingConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(GameAssignment.class)
                       .filter(gameAssignment -> gameAssignment.getIndexInGame() == 0 && gameAssignment.getReferee().isNonExist())
                       .penalizeConfigurable(RefereeConstraintConfiguration.FIRST_REFEREE_IS_NOT_NON_EXIST);
    }
    
    public Constraint sameRefereeMultipleGameIndexConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Game.class)
                       .filter(game -> {
                           Referee referee1 = null;
                           Referee referee2 = null;
                    
                           boolean result = false;
                           if ((referee1 = game.getAssignments().get(0).getReferee()) != null && (referee2 = game.getAssignments().get(1).getReferee()) != null) {
                               result = referee1.equals(referee2);
                           }
                           if ((referee1 = game.getAssignments().get(1).getReferee()) != null && (referee2 = game.getAssignments().get(2).getReferee()) != null) {
                               result = result || referee1.equals(referee2);
                           }
                           if ((referee1 = game.getAssignments().get(2).getReferee()) != null && (referee2 = game.getAssignments().get(0).getReferee()) != null) {
                               result = result || referee1.equals(referee2);
                           }
                           return result;
                       })
                       .penalizeConfigurable(RefereeConstraintConfiguration.SAME_REFEREE_MULTIPLE_GAME_INDEX);
    }
    
    public Constraint isInAvailabilityConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Referee.class)
                       .filter(referee -> !referee.isNonExist())
                       .penalizeConfigurable(RefereeConstraintConfiguration.IS_IN_AVAILABILITY_CONSTRAINT, referee -> {
                           int hardAmount = 0;
    
                           ArrayList<GameAssignment> gameAssignments = new ArrayList<>(referee.getAssignments());
                           gameAssignments.sort(new GameAssignmentComparator());
    
                           GameAssignment firstGameAssignment = referee.getAssignments().get(0);
                           //if the journey from home to the first assignment is ok
                           //time in minutes
                           long fromHomeTravelTime = referee.getHomeLocation().getTravelTimeInMinutes(firstGameAssignment.getGame().getGameLocation());
    
                           TimePeriod homeToGamePeriod = TimePeriod.builder()
                                                                 .start(firstGameAssignment.getGame().getGameRefereePeriod().getStart().minusMinutes(fromHomeTravelTime))
                                                                 .end(firstGameAssignment.getGame().getGameRefereePeriod().getEnd())
                                                                 .build();
                           if (!referee.checkIfAvailable(homeToGamePeriod)) {
                               hardAmount += 1;
                           }
                           //Check the current game period plus the travel time to the next period
                           for (int i = 0; i < gameAssignments.size(); i++) {
                               GameAssignment currentGameAssignment = gameAssignments.get(i);
                               GameAssignment nextGameAssignment = gameAssignments.get(i + 1);
                               TimePeriod fullTimePeriod = TimePeriod.builder()
                                                                   .start(currentGameAssignment.getGame().getGameRefereePeriod().getStart())
                                                                   .end(currentGameAssignment.getGame().getGameRefereePeriod().getEnd()
                                                                                .plusMinutes(currentGameAssignment.getGame().getGameLocation()
                                                                                                     .getTravelTimeInMinutes(nextGameAssignment.getGame().getGameLocation())))
                                                                   .build();
                               if (!referee.checkIfAvailable(fullTimePeriod)) {
                                   TimePeriod fromGameToHomePeriod = TimePeriod.builder()
                                                                             .start(currentGameAssignment.getGame().getGameRefereePeriod().getStart())
                                                                             .end(currentGameAssignment.getGame().getGameRefereePeriod()
                                                                                          .getEnd().plusMinutes(currentGameAssignment.getGame().getGameLocation().getTravelTimeInMinutes(referee.getHomeLocation()))).build();
                                   if (referee.checkIfAvailable(fromGameToHomePeriod)) {
                                       TimePeriod fromHomeToGamePeriod = TimePeriod.builder()
                                                                                 .start(nextGameAssignment.getGame().getGameRefereePeriod().getStart().minusMinutes(referee.getHomeLocation().getTravelTimeInMinutes(nextGameAssignment.getGame().getGameLocation())))
                                                                                 .end(currentGameAssignment.getGame().getGameRefereePeriod().getStart()).build();
                                       if (!referee.checkIfAvailable(fromHomeToGamePeriod)) {
                                           hardAmount += 1;
                                       }
                                   } else {
                                       hardAmount += 1;
                                   }
                               }
                           }
                           return hardAmount;
                       });
    }
}