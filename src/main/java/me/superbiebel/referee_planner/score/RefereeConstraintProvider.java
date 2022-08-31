package me.superbiebel.referee_planner.score;

import io.quarkus.logging.Log;
import me.superbiebel.referee_planner.domain.*;
import me.superbiebel.referee_planner.domain.comparators.GameAssignmentComparator;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

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
                notEnoughRefereesConstraint(constraintFactory),
                inRangeConstraint(constraintFactory)
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
                           Map<Availability, List<GameAssignment>> availabilityGameAssignmentMap = referee.getAvailabilityToGameAssignmentsMap();
                           for (Map.Entry<Availability, List<GameAssignment>> entry : availabilityGameAssignmentMap.entrySet()) {
                               Availability availability = entry.getKey();
                               List<GameAssignment> assignmentList = entry.getValue();
                        
                               if (assignmentList.isEmpty()) {
                                   continue;
                               }
                        
                               assignmentList.sort(GameAssignmentComparator.COMPARATOR);
                               int inLoopAmount = 0;
                               inLoopAmount += availability.getStartLocation().getDistanceTo(assignmentList.get(0).getGame().getGameLocation());
                               for (int i = 0, assignmentListSize = assignmentList.size(); i < assignmentListSize; i++) {
                                   GameAssignment currentAssignment = assignmentList.get(i);
                                   if (i + 1 == assignmentListSize) break;
                                   GameAssignment nextAssignment = assignmentList.get(i + 1);
                                   long addAmount = currentAssignment.getGame().getGameLocation().getDistanceTo(nextAssignment.getGame().getGameLocation());
                                   inLoopAmount += addAmount;
                               }
    
                               if (availability.isEndLocationEnabled()) {
                                   inLoopAmount += assignmentList.get(assignmentList.size() - 1).getGame().getGameLocation().getDistanceTo(availability.getEndLocation());
                               }
    
                               totalAmount += inLoopAmount;
                           }
    
                           return totalAmount;
                       });
    }
    
    /*
    EVERY AVAILABILITY IS A BLOCK OF ITS OWN! THERE WILL BE NO CALCULATING THE DISTANCE IN BETWEEN AVAILABILITIES!!!
    */
    private Constraint inRangeConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Referee.class)
                       .filter(referee -> !referee.isNonExist())
                       .penalizeConfigurable(RefereeConstraintConfiguration.MAX_RANGE, referee -> {
                           int totalAmount = 0;
                           Map<Availability, List<GameAssignment>> availabilityGameAssignmentMap = referee.getAvailabilityToGameAssignmentsMap();
                           for (Map.Entry<Availability, List<GameAssignment>> entry : availabilityGameAssignmentMap.entrySet()) {
                               Availability availability = entry.getKey();
                               List<GameAssignment> assignmentList = entry.getValue();
                        
                               if (assignmentList.isEmpty()) {
                                   continue;
                               }
                        
                               assignmentList.sort(GameAssignmentComparator.COMPARATOR);
                               int inLoopAmount = 0;
                               inLoopAmount += checkForRangeViolation(availability, availability.getStartLocation().getDistanceTo(assignmentList.get(0).getGame().getGameLocation()));
                               for (int i = 0, assignmentListSize = assignmentList.size(); i < assignmentListSize; i++) {
                                   GameAssignment currentAssignment = assignmentList.get(i);
                                   if (i + 1 == assignmentListSize) break;
                                   GameAssignment nextAssignment = assignmentList.get(i + 1);
                                   long addAmount = checkForRangeViolation(availability, currentAssignment.getGame().getGameLocation().getDistanceTo(nextAssignment.getGame().getGameLocation()));
                                   inLoopAmount += addAmount;
                               }
                        
                               if (availability.isEndLocationEnabled()) {
                                   inLoopAmount += checkForRangeViolation(availability, assignmentList.get(assignmentList.size() - 1).getGame().getGameLocation().getDistanceTo(availability.getEndLocation()));
                               }
                        
                               totalAmount += inLoopAmount;
                           }
                    
                           return totalAmount;
                       });
    }
    public static long checkForRangeViolation(Availability availability, long amount) {
        if (availability.isMaxRangeEnabled() && amount > availability.getMaxRange()) {
            return amount - availability.getMaxRange();
        } else return 0;
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
                       .penalizeConfigurableLong(RefereeConstraintConfiguration.NOT_ENOUGH_REFEREES, (game, count) -> (long) game.getPriority() * count);
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
                       .penalizeConfigurable(RefereeConstraintConfiguration.IS_PHYSICALLY_POSSIBLE, referee -> {
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
                           Map<Availability, List<GameAssignment>> availabilityGameAssignmentMap = referee.getAvailabilityToGameAssignmentsMap();
                           if (!referee.getUnassignedAssignments().isEmpty()) {
                               amount += referee.getUnassignedAssignments().size();
                               earlyAbort = true;
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
                                       if (availability.isEndLocationEnabled() && currentGameAssignment.getGame().getGameRefereePeriod().getEnd().isAfter(availabilityPeriod.getEnd().minusMinutes(currentGameAssignment.getGame().getGameLocation().getTravelTimeInMinutes(availability.getEndLocation())))) {
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
    
}
