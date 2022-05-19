package me.superbiebel.referee_planner;

import io.quarkus.logging.Log;
import me.superbiebel.referee_planner.domain.*;
import me.superbiebel.referee_planner.domain.comparators.GameAssignmentComparator;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                           int totalAmount = 0;
                           Map<Availability, List<GameAssignment>> availabilityGameAssignmentMap = new HashMap<>();
                           for (GameAssignment gameAssignment : referee.getAssignments()) {
                               Availability availability;
                               List<GameAssignment> assignmentList;
                               if ((availability = referee.getCorrespondingAvailability(gameAssignment)) != null) {
                                   if ((assignmentList = availabilityGameAssignmentMap.get(availability)) == null) {
                                       assignmentList = new ArrayList<>();
                                       assignmentList.add(gameAssignment);
                                       availabilityGameAssignmentMap.put(availability, assignmentList);
                                   } else {
                                       assignmentList.add(gameAssignment);
                                   }
                               }
                           }
                           for (Map.Entry<Availability, List<GameAssignment>> entry : availabilityGameAssignmentMap.entrySet()) {
                               Availability availability = entry.getKey();
                               List<GameAssignment> assignmentList = entry.getValue();
                               assignmentList.sort(GameAssignmentComparator.COMPARATOR);
                               int inLoopAmount = 0;
                               inLoopAmount += availability.getStartLocation().getDistanceTo(assignmentList.get(0).getGame().getGameLocation());
                               for (int i = 0, assignmentListSize = assignmentList.size(); i < assignmentListSize; i++) {
                                   GameAssignment currentAssignment = assignmentList.get(i);
                                   if (i + 1 == assignmentListSize && availability.isEndLocationEnabled()) {
                                       inLoopAmount += currentAssignment.getGame().getGameLocation().getDistanceTo(availability.getEndLocation());
                                   }
                                   GameAssignment nextAssignment = assignmentList.get(i + 1);
                                   inLoopAmount += currentAssignment.getGame().getGameLocation().getDistanceTo(nextAssignment.getGame().getGameLocation());
                               }
                               totalAmount += inLoopAmount;
                           }
    
                           return totalAmount;
                       });
                           /*-------OLD IMPLEMENTATION--------
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
                           return (int) amount;*/
    
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
                       }).groupBy(GameAssignment::getGame, ConstraintCollectors.count())
                       .penalizeConfigurableLong(RefereeConstraintConfiguration.NOT_ENOUGH_REFEREES, (assignment, count) -> (long) assignment.getPriority() *count);
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
                           /*
                           What this block of code actually tries to accomplish
                           1. Assign every gameAssignment to the availability it belongs.
                              If it can't be linked to an availability then it is already outside them and a hard score should be given.
                           2. Check in each availability if everything is physically and still fall in between the availability time timePeriod.
                           
                           EVERY AVAILABILITY IS A BLOCK OF ITS OWN! THERE WILL BE NO CALCULATING THE DISTANCE IN BETWEEN AVAILABILITIES!!!
                            */
                           int amount = 0;
                           boolean earlyAbort = false;
                    
                           // action described in 1.
                           Map<Availability, List<GameAssignment>> availabilityGameAssignmentMap = new HashMap<>();
                           for (GameAssignment gameAssignment : referee.getAssignments()) {
                               Availability availability;
                               List<GameAssignment> assignmentList;
                               if ((availability = referee.getCorrespondingAvailability(gameAssignment)) != null) {
                                   if ((assignmentList = availabilityGameAssignmentMap.get(availability)) == null) {
                                       assignmentList = new ArrayList<>();
                                       assignmentList.add(gameAssignment);
                                       availabilityGameAssignmentMap.put(availability, assignmentList);
                                   } else {
                                       assignmentList.add(gameAssignment);
                                   }
                               } else {
                                   amount++;
                                   earlyAbort = true;
                               }
                           }
                           if (earlyAbort) {
                               return amount;
                           }
                    
                           //action described in 2.
                           for (Map.Entry<Availability, List<GameAssignment>> entry : availabilityGameAssignmentMap.entrySet()) {
                               List<GameAssignment> assignmentList = entry.getValue();
                               if (assignmentList.isEmpty()) {
                                   continue;
                               }
                               Availability availability = entry.getKey();
    
                               assignmentList.sort(GameAssignmentComparator.COMPARATOR);
                               TimePeriod availabilityPeriod = availability.getTimePeriod();
    
                               //checks if we can actually get from the start location to the first game on time
                               long fromBeginTravelTime = availability.getStartLocation().getTravelTimeInMinutes(assignmentList.get(0).getGame().getGameLocation());
                               if (availabilityPeriod.getStart().isAfter(assignmentList.get(0).getGame().getGameRefereePeriod().getStart().minusMinutes(fromBeginTravelTime))) {
                                   amount++;
                                   earlyAbort = true;
                                   continue;
                               }
    
                               //checks if we assignments don't overlap
                               for (int i = 0, assignmentListSize = assignmentList.size(); i < assignmentListSize; i++) {
                                   GameAssignment currentGameAssignment = assignmentList.get(i);
                                   if (i + 1 == assignmentListSize) {
                                       //if we can't actually get to the end location on time
                                       if (currentGameAssignment.getGame().getGameRefereePeriod().getEnd().isAfter(availabilityPeriod.getEnd().minusMinutes(currentGameAssignment.getGame().getGameLocation().getTravelTimeInMinutes(availability.getEndLocation())))) {
                                           amount++;
                                           break;
                                       } else {
                                           continue;
                                       }
                                   }
        
                                   GameAssignment nextGameAssignment = assignmentList.get(i + 1);
                                   if (currentGameAssignment.getGame().getGameLocation().equals(nextGameAssignment.getGame().getGameLocation())) {
                                       if (currentGameAssignment.getGame().getGameRefereePeriod().getEnd().equals(nextGameAssignment.getGame().getGamePeriod().getStart())
                                                   || currentGameAssignment.getGame().getGameRefereePeriod().getEnd().isBefore(nextGameAssignment.getGame().getGamePeriod().getStart())) {
                                           continue;
                                       } else if (currentGameAssignment.getGame().getGameRefereePeriod().getEnd().isAfter(nextGameAssignment.getGame().getGamePeriod().getStart())) {
                                           amount++;
                                           break;
                                       }
                                   } else {
                                       if (currentGameAssignment.getGame().getGameRefereePeriod().getEnd()
                                                   .isAfter(nextGameAssignment.getGame().getGameRefereePeriod().getStart()
                                                                    .minusMinutes(currentGameAssignment.getGame().getGameLocation().getTravelTimeInMinutes(nextGameAssignment.getGame().getGameLocation())))) {
                                           amount++;
                                           break;
                                       } else {
                                           continue;
                                       }
                                   }
                               }
    
                           }
                           if (earlyAbort) {
                               return amount;
                           }
                    
                           return amount;
                       });
    }
                           /* ------
                           int amount = 0;
                           if (referee.getAssignments().isEmpty()) {
                               return 0;
                           }
                    
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
                           //Log.tracef("Home to game timePeriod begin: %s and end: %s", homeToGamePeriod1.getStart(), homeToGamePeriod1.getEnd());
                           if (!referee.checkIfAvailable(homeToGamePeriod1)) {
                               amount++;
                           }
                           //Check the current game timePeriod plus the travel time to the next timePeriod
                           for (int i = 0; i < gameAssignments.size(); i++) {
                               GameAssignment currentGameAssignment = gameAssignments.get(i);
                               //if it is the last gameAssignment
                               if (gameAssignments.size() == i + 1) {
                                   TimePeriod fromGameToHomePeriod1 = TimePeriod.builder()
                                                                              .start(currentGameAssignment.getGame().getGameRefereePeriod().getStart())
                                                                              .end(currentGameAssignment.getGame().getGameRefereePeriod()
                                                                                           .getEnd().plusMinutes(currentGameAssignment.getGame().getGameLocation().getTravelTimeInMinutes(referee.getHomeLocation()))).build();
                                   //Log.tracef("game to home timePeriod (end) iteration: %s; begin: %s and end: %s", i, fromGameToHomePeriod1.getStart(), fromGameToHomePeriod1.getEnd());
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
                               //Log.tracef("full time timePeriod iteration: %s; begin: %s and end: %s", i, loopFullTimePeriod.getStart(), loopFullTimePeriod.getEnd());
                               if (!referee.checkIfAvailable(loopFullTimePeriod)) {
                                   TimePeriod fromGameToHomePeriod2 = TimePeriod.builder()
                                                                              .start(currentGameAssignment.getGame().getGameRefereePeriod().getStart())
                                                                              .end(currentGameAssignment.getGame().getGameRefereePeriod()
                                                                                           .getEnd().plusMinutes(currentGameAssignment.getGame().getGameLocation().getTravelTimeInMinutes(referee.getHomeLocation()))).build();
                                   //Log.tracef("game to home timePeriod iteration: %s; begin: %s and end: %s", i, fromGameToHomePeriod2.getStart(), fromGameToHomePeriod2.getEnd());
                                   if (referee.checkIfAvailable(fromGameToHomePeriod2)) {
                                       TimePeriod fromHomeToGamePeriod2 = TimePeriod.builder()
                                                                                  .start(nextGameAssignment.getGame().getGameRefereePeriod().getStart().minusMinutes(referee.getHomeLocation().getTravelTimeInMinutes(nextGameAssignment.getGame().getGameLocation())))
                                                                                  .end(currentGameAssignment.getGame().getGameRefereePeriod().getStart()).build();
                                       //Log.tracef("home to game timePeriod iteration: %s; begin: %s and end: %s", i, fromGameToHomePeriod2.getStart(), fromGameToHomePeriod2.getEnd());
                                       if (!referee.checkIfAvailable(fromHomeToGamePeriod2)) {
                                           amount++;
                                       }
                                   } else {
                                       amount++;
                                   }
                               }
                           }
                    
                           return amount;*/
    
    
}
