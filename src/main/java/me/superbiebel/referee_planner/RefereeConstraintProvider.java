package me.superbiebel.referee_planner;

import io.quarkus.logging.Log;
import me.superbiebel.referee_planner.domain.*;
import me.superbiebel.referee_planner.domain.comparators.GameAssignmentComparator;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

import java.util.ArrayList;
import java.util.List;

public class RefereeConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                sufficientHardMinimumExperienceLevel(constraintFactory),
                sufficientSoftMinimumExperienceLevel(constraintFactory),
                sufficientSoftMaximumExperienceLevel(constraintFactory),
                distanceSoftConstraint(constraintFactory),
                tooManyRefereesConstraint(constraintFactory),
                nonExistingAlwaysLowerIndex(constraintFactory),
                sameRefereeMultipleGameIndexConstraint(constraintFactory),
                isPhysicallyPossibleConstraint(constraintFactory),
                notEnoughRefereesConstraint(constraintFactory)
        };
    }
    
    public Constraint sufficientHardMinimumExperienceLevel(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(GameAssignment.class)
                       .filter(gameAssignment -> !gameAssignment.getReferee().isNonExist())
                       .filter(gameAssignment -> gameAssignment.getGame().getHardMinimumExperience() > gameAssignment.getReferee().getExperience())
                       .penalizeConfigurable(RefereeConstraintConfiguration.SUFFICIENT_HARD_MINIMUM_EXPERIENCE_LEVEL, gameAssignment -> (gameAssignment.getGame().getHardMinimumExperience() - gameAssignment.getReferee().getExperience()) * gameAssignment.getGame().getPriority());
    }
    public Constraint sufficientSoftMinimumExperienceLevel(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(GameAssignment.class)
                       .filter(gameAssignment -> !gameAssignment.getReferee().isNonExist())
                       .filter(gameAssignment -> gameAssignment.getGame().getSoftMinimumExperience() > gameAssignment.getReferee().getExperience())
                       .penalizeConfigurable(RefereeConstraintConfiguration.SUFFICIENT_SOFT_MINIMUM_EXPERIENCE_LEVEL, gameAssignment -> (gameAssignment.getGame().getSoftMinimumExperience() - gameAssignment.getReferee().getExperience()) * gameAssignment.getGame().getPriority());
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
                           amount += referee.getHomeLocation().getDistanceTo(gameAssignments.get(0).getGame().getGameLocation());
                           
                           for (GameAssignment assignment : gameAssignments) {
                               if (lastAssignment != null) {
                                   long addingAmount = lastAssignment.getGame().getGameLocation().getDistanceTo(assignment.getGame().getGameLocation());
                                   amount += addingAmount;
                               }
                               lastAssignment = assignment;
                           }
                           return (int) amount;
                       });
    }
    
    public Constraint notEnoughRefereesConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(GameAssignment.class)
                       .filter(gameAssignment -> {
                           Log.tracef("notEnoughRefereesCheck not null is %s for game %s with index %s", gameAssignment.getReferee() != null, gameAssignment.getGame().getGameUUID(), gameAssignment.getIndexInGame());
                           return gameAssignment.getReferee() != null;
                       })
                       .filter(gameAssignment -> {
                           Log.tracef("notEnoughRefereesCheck isNonExist is %s for game %s with index %s", gameAssignment.getReferee().isNonExist(), gameAssignment.getGame().getGameUUID(), gameAssignment.getIndexInGame());
                           return gameAssignment.getReferee().isNonExist();
                       })
                       .penalizeConfigurableLong(RefereeConstraintConfiguration.NOT_ENOUGH_REFEREES, assignment -> assignment.getGame().getPriority());
    }
    
    public Constraint tooManyRefereesConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Game.class)
                       .filter(game -> game.getAssignments()
                                               .stream()
                                               .filter(gameAssignment -> gameAssignment.getReferee() != null)
                                               .filter(gameAssignment -> !gameAssignment.getReferee().isNonExist()).count() > game.getAmountOfRefereesNeeded())
                       .penalizeConfigurable(RefereeConstraintConfiguration.TOO_MUCH_REFEREES);
    }
    
    @SuppressWarnings("ConstantConditions")
    public Constraint nonExistingAlwaysLowerIndex(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Game.class)
                       .penalizeConfigurable(RefereeConstraintConfiguration.NONEXIST_REF_NOT_ON_LOWER_INDEX_EXIST_REFEREE, game -> {
                           int count = 0;
                           GameAssignment assignment0 = null;
                           GameAssignment assignment1 = null;
                           GameAssignment assignment2 = null;
                    
                           for (GameAssignment gameAssignment : game.getAssignments()) {
                               if (gameAssignment.getIndexInGame() == 0) {
                                   assignment0 = gameAssignment;
                               } else if (gameAssignment.getIndexInGame() == 1) {
                                   assignment1 = gameAssignment;
                               } else if (gameAssignment.getIndexInGame() == 2) {
                                   assignment2 = gameAssignment;
                               }
                           }
                           if (assignment0.getReferee() != null && assignment0.getReferee().isNonExist()) {
                               if (assignment1.getReferee() != null && !assignment1.getReferee().isNonExist()) {
                                   count++;
                               }
                               if (assignment2.getReferee() != null && !assignment2.getReferee().isNonExist()) {
                                   count++;
                               }
                           } else if (assignment1.getReferee() != null && assignment1.getReferee().isNonExist() && assignment2.getReferee() != null && !assignment2.getReferee().isNonExist()) {
                               count++;
                           }
                           return count;
                       });
    }
    
    public Constraint sameRefereeMultipleGameIndexConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Game.class)
                       .filter(game -> {
                           Referee referee1;
                           Referee referee2;
                    
                           boolean result = false;
                           if ((referee1 = game.getAssignments().get(0).getReferee()) != null && (referee2 = game.getAssignments().get(1).getReferee()) != null) {
                               result = referee1.equals(referee2);
                           }
                           if (game.getAssignments().size() == 1) {
                               return result;
                           }
                           if ((referee1 = game.getAssignments().get(1).getReferee()) != null && (referee2 = game.getAssignments().get(2).getReferee()) != null) {
                               result = result || referee1.equals(referee2);
                           }
                           if (game.getAssignments().size() == 2) {
                               return result;
                           }
                           if ((referee1 = game.getAssignments().get(2).getReferee()) != null && (referee2 = game.getAssignments().get(0).getReferee()) != null) {
                               result = result || referee1.equals(referee2);
                           }
                           return result;
                       })
                       .penalizeConfigurable(RefereeConstraintConfiguration.SAME_REFEREE_MULTIPLE_GAME_INDEX);
    }
    
    public Constraint isPhysicallyPossibleConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Referee.class)
                       .filter(referee -> !referee.isNonExist())
                       .penalizeConfigurable(RefereeConstraintConfiguration.IS_IN_AVAILABILITY_CONSTRAINT, referee -> {
                           int amount = 0;
                           if (referee.getAssignments().isEmpty()) {
                               return 0;
                           }
                           //put every timeperiod in here, so we can check if they ever overlap
                           List<TimePeriod> bigTimePeriodList = new ArrayList<>();
                    
                           ArrayList<GameAssignment> gameAssignments = new ArrayList<>(referee.getAssignments());
                           gameAssignments.sort(new GameAssignmentComparator());
                    
                           //check if the gameAssignments don't actually overlap over each other
                           for (int i = 0; i < gameAssignments.size(); i++) {
                               GameAssignment currentGameAssignment = gameAssignments.get(i);
                               if (gameAssignments.size() == i + 1) {
                                   continue;
                               }
                               GameAssignment nextGameAssignment = gameAssignments.get(i + 1);
                        
                               GameAssignment firstGameAssignment = gameAssignments.get(0);
                               long fromHomeTravelTime = referee.getHomeLocation().getTravelTimeInMinutes(firstGameAssignment.getGame().getGameLocation());
                        
                               TimePeriod minimumTimePeriod = TimePeriod.builder().start(currentGameAssignment.getGame().getGameRefereePeriod().getStart().minusMinutes(fromHomeTravelTime))
                                                                      .end(currentGameAssignment.getGame().getGameRefereePeriod().getEnd().plusMinutes(currentGameAssignment.getGame().getGameLocation().getTravelTimeInMinutes(nextGameAssignment.getGame().getGameLocation())))
                                                                      .build();
                               if (minimumTimePeriod.doesOverLap(nextGameAssignment.getGame().getGameRefereePeriod())) {
                                   amount++;
                               }
                           }
                           if (amount != 0) {
                               return amount;
                           }
                           //-------check if everything falls in the availability of that person----
                           GameAssignment firstGameAssignment = gameAssignments.get(0);
                           //if the journey from home to the first assignment is ok
                           //time in minutes
                           long fromHomeTravelTime = referee.getHomeLocation().getTravelTimeInMinutes(firstGameAssignment.getGame().getGameLocation());
                           //Log.tracef("fromHomeTravelTime first time: %s", fromHomeTravelTime);
                    
                           TimePeriod homeToGamePeriod1 = TimePeriod.builder()
                                                                  .start(firstGameAssignment.getGame().getGameRefereePeriod().getStart().minusMinutes(fromHomeTravelTime))
                                                                  .end(firstGameAssignment.getGame().getGameRefereePeriod().getStart())
                                                                  .build();
                           bigTimePeriodList.add(homeToGamePeriod1);
                           //Log.tracef("Home to game period begin: %s and end: %s", homeToGamePeriod1.getStart(), homeToGamePeriod1.getEnd());
                           if (!referee.checkIfAvailable(homeToGamePeriod1)) {
                               amount++;
                           }
                           //Check the current game period plus the travel time to the next period
                           for (int i = 0; i < gameAssignments.size(); i++) {
                               GameAssignment currentGameAssignment = gameAssignments.get(i);
                               //if it is the last gameAssignment
                               if (gameAssignments.size() == i + 1) {
                                   TimePeriod fromGameToHomePeriod1 = TimePeriod.builder()
                                                                              .start(currentGameAssignment.getGame().getGameRefereePeriod().getStart())
                                                                              .end(currentGameAssignment.getGame().getGameRefereePeriod()
                                                                                           .getEnd().plusMinutes(currentGameAssignment.getGame().getGameLocation().getTravelTimeInMinutes(referee.getHomeLocation()))).build();
                                   bigTimePeriodList.add(fromGameToHomePeriod1);
                                   //Log.tracef("game to home period (end) iteration: %s; begin: %s and end: %s", i, fromGameToHomePeriod1.getStart(), fromGameToHomePeriod1.getEnd());
                                   if (!referee.checkIfAvailable(fromGameToHomePeriod1)) {
                                       amount++;
                                   }
                                   break;
                               }
                               GameAssignment nextGameAssignment = gameAssignments.get(i + 1);
                        
                               TimePeriod loopFullTimePeriod = TimePeriod.builder()
                                                                       .start(currentGameAssignment.getGame().getGameRefereePeriod().getStart())
                                                                       .end(currentGameAssignment.getGame().getGameRefereePeriod().getEnd()
                                                                                    .plusMinutes(currentGameAssignment.getGame().getGameLocation()
                                                                                                         .getTravelTimeInMinutes(nextGameAssignment.getGame().getGameLocation())))
                                                                       .build();
                               bigTimePeriodList.add(loopFullTimePeriod);
                               //Log.tracef("full time period iteration: %s; begin: %s and end: %s", i, loopFullTimePeriod.getStart(), loopFullTimePeriod.getEnd());
                               if (!referee.checkIfAvailable(loopFullTimePeriod)) {
                                   TimePeriod fromGameToHomePeriod2 = TimePeriod.builder()
                                                                              .start(currentGameAssignment.getGame().getGameRefereePeriod().getStart())
                                                                              .end(currentGameAssignment.getGame().getGameRefereePeriod()
                                                                                           .getEnd().plusMinutes(currentGameAssignment.getGame().getGameLocation().getTravelTimeInMinutes(referee.getHomeLocation()))).build();
                                   bigTimePeriodList.add(fromGameToHomePeriod2);
                                   //Log.tracef("game to home period iteration: %s; begin: %s and end: %s", i, fromGameToHomePeriod2.getStart(), fromGameToHomePeriod2.getEnd());
                                   if (referee.checkIfAvailable(fromGameToHomePeriod2)) {
                                       TimePeriod fromHomeToGamePeriod2 = TimePeriod.builder()
                                                                                  .start(nextGameAssignment.getGame().getGameRefereePeriod().getStart().minusMinutes(referee.getHomeLocation().getTravelTimeInMinutes(nextGameAssignment.getGame().getGameLocation())))
                                                                                  .end(currentGameAssignment.getGame().getGameRefereePeriod().getStart()).build();
                                       bigTimePeriodList.add(fromHomeToGamePeriod2);
                                       //Log.tracef("home to game period iteration: %s; begin: %s and end: %s", i, fromGameToHomePeriod2.getStart(), fromGameToHomePeriod2.getEnd());
                                       if (!referee.checkIfAvailable(fromHomeToGamePeriod2)) {
                                           amount++;
                                       }
                                   } else {
                                       amount++;
                                   }
                               }
                           }
                    
                           return amount;
                       });
    }
}
