package me.superbiebel.tests;

import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import me.superbiebel.referee_planner.domain.*;
import me.superbiebel.referee_planner.domain.comparators.GameAssignmentComparator;
import me.superbiebel.referee_planner.domain.data.RandomDataGenerator;
import me.superbiebel.referee_planner.score.RefereeConstraintProvider;
import me.superbiebel.referee_planner.variablelisteners.AvailabilityAssignmentMapVariableListener;
import org.junit.jupiter.api.*;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@QuarkusTest
class ConstraintProviderTests {
    
    @Inject
    ConstraintVerifier<RefereeConstraintProvider, RefereeTimeTable> constraintVerifier;
    
    @Test
    void hardMinimumExperienceTest() {
        Referee referee = Referee.builder().experience(0).build();
        GameAssignment gameAssignment = GameAssignment.builder()
                                                .game(RandomDataGenerator.generateGame().toBuilder().hardMinimumExperience(10).priority(10).build())
                                                .referee(referee).build();
        referee.addAssignment(gameAssignment);
    
        constraintVerifier.verifyThat(RefereeConstraintProvider::sufficientHardMinimumExperienceLevel)
                .given(referee, gameAssignment)
                .penalizesBy(100);
    }
    @Test
    void softMinimumExperienceTest() {
        Referee referee = Referee.builder().experience(0).build();
        GameAssignment gameAssignment = GameAssignment.builder()
                                                .game(RandomDataGenerator.generateGame().toBuilder().softMinimumExperience(10).priority(10).build())
                                                .referee(referee).build();
        referee.addAssignment(gameAssignment);
    
        constraintVerifier.verifyThat(RefereeConstraintProvider::sufficientSoftMinimumExperienceLevel)
                .given(referee, gameAssignment)
                .penalizesBy(100);
    }
    
    @Test
    void softMaximumExperienceTest() {
        Referee referee = Referee.builder().experience(20).build();
        GameAssignment gameAssignment = GameAssignment.builder()
                                                .game(RandomDataGenerator.generateGame().toBuilder().softMaximumExperience(10).build())
                                                .referee(referee).build();
        referee.addAssignment(gameAssignment);
        
        constraintVerifier.verifyThat(RefereeConstraintProvider::sufficientSoftMaximumExperienceLevel)
                .given(referee, gameAssignment)
                .penalizesBy(10);
    }
    
    @Test
    void distanceSoftConstraintTest() {
        //ConstraintVerifier<RefereeConstraintProvider, RefereeTimeTable> newVerifier
          //      = ConstraintVerifier.build(new RefereeConstraintProvider(), RefereeTimeTable.class, Referee.class, GameAssignment.class);
        
        Location startLocation = Location.builder()
                                         .latitude(50.900103554473006)
                                         .longitude(4.740255725222106)
                                         .build();
        Location location1 = Location.builder()
                                     .latitude(50.95861155236005)
                                     .longitude(4.827628078154828)
                                     .build();
        Location location2 = Location.builder()
                                     .latitude(51.0365272041762)
                                     .longitude(4.942546401059978)
                                     .build();
        Location location3 = Location.builder()
                                     .latitude(51.069702174341366)
                                     .longitude(5.045459586990766)
                                     .build();
        Location location4 = Location.builder()
                                     .latitude(50.95930351484321)
                                     .longitude(4.617974045432108)
                                     .build();
        Location endLocation = Location.builder()
                                       .latitude(51.95930351484321)
                                       .longitude(5.617974045432108)
                                       .build();
        TimePeriod availabilityPeriod = TimePeriod.builder().start(RandomDataGenerator.BEGIN_REFEREE_DATE_TIME).end(RandomDataGenerator.END_REFEREE_DATE_TIME).build();
        Availability availability1 = Availability.builder().startLocation(startLocation).endLocation(endLocation).endLocationEnabled(true).timePeriod(availabilityPeriod).build();
    
        List<Availability> availabilityList = new ArrayList<>();
        availabilityList.add(availability1);
    
        Referee referee = RandomDataGenerator.generateReferee().toBuilder().availabilityList(availabilityList).build();
    
        GameAssignment gameAssignment1 = GameAssignment.builder().game(RandomDataGenerator.generateGame().toBuilder().gameLocation(location1).gamePeriod(TimePeriod.builder().start(RandomDataGenerator.BEGIN_GAME_DATE_TIME.plusHours(1)).end(RandomDataGenerator.BEGIN_GAME_DATE_TIME.plusHours(3)).build()).build()).build();
        GameAssignment gameAssignment2 = GameAssignment.builder().game(RandomDataGenerator.generateGame().toBuilder().gameLocation(location2).gamePeriod(TimePeriod.builder().start(RandomDataGenerator.BEGIN_GAME_DATE_TIME.plusHours(4)).end(RandomDataGenerator.BEGIN_GAME_DATE_TIME.plusHours(6)).build()).build()).build();
        GameAssignment gameAssignment3 = GameAssignment.builder().game(RandomDataGenerator.generateGame().toBuilder().gameLocation(location3).gamePeriod(TimePeriod.builder().start(RandomDataGenerator.BEGIN_GAME_DATE_TIME.plusHours(7)).end(RandomDataGenerator.BEGIN_GAME_DATE_TIME.plusHours(9)).build()).build()).build();
        GameAssignment gameAssignment4 = GameAssignment.builder().game(RandomDataGenerator.generateGame().toBuilder().gameLocation(location4).gamePeriod(TimePeriod.builder().start(RandomDataGenerator.BEGIN_GAME_DATE_TIME.plusHours(10)).end(RandomDataGenerator.BEGIN_GAME_DATE_TIME.plusHours(12)).build()).build()).build();
    
        referee.addAssignment(gameAssignment1);
        referee.addAssignment(gameAssignment2);
        referee.addAssignment(gameAssignment3);
        referee.addAssignment(gameAssignment4);
    
        referee.getSortedAssignments().addAll(referee.getAssignments());
        referee.getSortedAssignments().sort(GameAssignmentComparator.COMPARATOR);
        AvailabilityAssignmentMapVariableListener.Pair<Map<Availability, List<GameAssignment>>, List<GameAssignment>> pair = AvailabilityAssignmentMapVariableListener.generateAvailabilityGameAssignmentMap(referee);
        referee.setAvailabilityToGameAssignmentsMap(pair.getLeft());
        referee.setUnassignedAssignments(pair.getRight());
        long startTo1 = startLocation.getDistanceTo(location1);
        Log.warn("startTo1: " + startTo1);
        long loc1To2 = location1.getDistanceTo(location2);
        Log.warn("loc1To2: " + loc1To2);
        long loc2To3 = location2.getDistanceTo(location3);
        Log.warn("loc2To3: " + loc2To3);
        long loc3To4 = location3.getDistanceTo(location4);
        Log.warn("loc3To4: " + loc3To4);
        long loc4ToEnd = location4.getDistanceTo(endLocation);
        Log.warn("loc4Toend: " + loc4ToEnd);
    
        constraintVerifier.verifyThat(RefereeConstraintProvider::distanceSoftConstraint)
                .given(referee).penalizesBy(startTo1 + loc1To2 + loc2To3 + loc3To4 + loc4ToEnd);
    }
    
    @Test
    
    void notEnoughRefereesConstraintTest() {
        Game game = RandomDataGenerator.generateGame().toBuilder().amountOfRefereesNeeded(3).priority(10).build();
        List<GameAssignment> gameAssignmentList = new ArrayList<>(Game.generateGameAssignments(game));
        GameAssignment gameAssignment0 = gameAssignmentList.get(0).toBuilder()
                                                 .indexInGame(0)
                                                 .game(game)
                                                 .referee(Referee.builder().isNonExist(false).build())
                                                 .build();
        GameAssignment gameAssignment1 = gameAssignmentList.get(1).toBuilder()
                                                 .indexInGame(1)
                                                 .game(game)
                                                 .referee(Referee.builder().isNonExist(false).build())
                                                 .build();
        GameAssignment gameAssignment2 = gameAssignmentList.get(2).toBuilder()
                                                 .indexInGame(2)
                                                 .game(game)
                                                 .referee(Referee.builder().isNonExist(true).build())
                                                 .build();
        game.setAssignments(gameAssignmentList);
    
        constraintVerifier.verifyThat(RefereeConstraintProvider::notEnoughRefereesConstraint)
                .given(gameAssignment0, gameAssignment1, gameAssignment2)
                .penalizesBy(10);
    }
    
    @Test
    
    void tooManyRefereesConstraintTest() {
        Game game = RandomDataGenerator.generateGame().toBuilder().amountOfRefereesNeeded(1).build();
        GameAssignment gameAssignment0 = GameAssignment.builder()
                                                 .indexInGame(0)
                                                 .game(game)
                                                 .referee(Referee.builder().isNonExist(false).build())
                                                 .build();
        GameAssignment gameAssignment1 = GameAssignment.builder()
                                                 .indexInGame(1)
                                                 .game(game)
                                                 .referee(Referee.builder().isNonExist(false).build())
                                                 .build();
        GameAssignment gameAssignment2 = GameAssignment.builder()
                                                 .indexInGame(2)
                                                 .game(game)
                                                 .referee(Referee.builder().isNonExist(false).build())
                                                 .build();
        List<GameAssignment> gameAssignmentList = new ArrayList<>();
        gameAssignmentList.add(gameAssignment0);
        gameAssignmentList.add(gameAssignment1);
        gameAssignmentList.add(gameAssignment2);
        game.setAssignments(gameAssignmentList);
        
        constraintVerifier.verifyThat(RefereeConstraintProvider::tooManyRefereesConstraint)
                .given(game)
                .penalizesBy(1);
    }
    
    @Test
    
    void firstRefereeIsNotNonExistingConstraintTest() {
        Game game = RandomDataGenerator.generateGame().toBuilder().amountOfRefereesNeeded(3).build();
        GameAssignment gameAssignment0 = GameAssignment.builder()
                                                 .indexInGame(0)
                                                 .game(game)
                                                 .referee(Referee.builder().isNonExist(true).build())
                                                 .build();
        GameAssignment gameAssignment1 = GameAssignment.builder()
                                                 .indexInGame(1)
                                                 .game(game)
                                                 .referee(Referee.builder().isNonExist(false).build())
                                                 .build();
        GameAssignment gameAssignment2 = GameAssignment.builder()
                                                 .indexInGame(2)
                                                 .game(game)
                                                 .referee(Referee.builder().isNonExist(false).build())
                                                 .build();
        List<GameAssignment> gameAssignmentList = new ArrayList<>();
        gameAssignmentList.add(gameAssignment0);
        gameAssignmentList.add(gameAssignment1);
        gameAssignmentList.add(gameAssignment2);
        game.setAssignments(gameAssignmentList);
    
        constraintVerifier.verifyThat(RefereeConstraintProvider::nonExistingAlwaysLowerIndex)
                .given(game)
                .penalizesBy(2);
    }
    
    @Test
    
    void sameRefereeMultipleGameIndexConstraintTest() {
        Game game = RandomDataGenerator.generateGame().toBuilder().amountOfRefereesNeeded(3).build();
        Referee referee = Referee.builder().isNonExist(false).build();
        GameAssignment gameAssignment0 = GameAssignment.builder()
                                                 .indexInGame(0)
                                                 .game(game)
                                                 .referee(referee)
                                                 .build();
        GameAssignment gameAssignment1 = GameAssignment.builder()
                                                 .indexInGame(1)
                                                 .game(game)
                                                 .referee(referee)
                                                 .build();
        GameAssignment gameAssignment2 = GameAssignment.builder()
                                                 .indexInGame(2)
                                                 .game(game)
                                                 .referee(referee)
                                                 .build();
        List<GameAssignment> gameAssignmentList = new ArrayList<>();
        gameAssignmentList.add(gameAssignment0);
        gameAssignmentList.add(gameAssignment1);
        gameAssignmentList.add(gameAssignment2);
        game.setAssignments(gameAssignmentList);
    
        constraintVerifier.verifyThat(RefereeConstraintProvider::sameRefereeMultipleGameIndexConstraint)
                .given(game)
                .penalizesBy(1);
    }
    
    @Test
    void isPhysicallyPossibleConstraintTest() {
        List<Availability> availabilityList = new ArrayList<>();
    
        Location homeLocation = Location.builder()
                                        .latitude(51)
                                        .longitude(4.8).build();
    
        LocalDateTime baseTime = LocalDateTime.now();
    
        TimePeriod timePeriod1 = TimePeriod.builder()
                                         .start(baseTime)
                                         .end(baseTime.plusHours(10))
                                         .build();
        TimePeriod timePeriod2 = TimePeriod.builder()
                                         .start(baseTime.plusHours(12))
                                         .end(baseTime.plusHours(22))
                                         .build();
    
        Availability availability1 = Availability.builder().timePeriod(timePeriod1).startLocation(homeLocation).build();
        Availability availability2 = Availability.builder().timePeriod(timePeriod2).startLocation(homeLocation).build();
    
        availabilityList.add(availability1);
        availabilityList.add(availability2);
    
        TimePeriod gameTimePeriod1 = TimePeriod.builder()
                                             .start(baseTime.plusHours(2))
                                             .end(baseTime.plusHours(4))
                                             .build();
        TimePeriod gameTimePeriod2 = TimePeriod.builder()
                                             .start(baseTime.plusHours(6))
                                             .end(baseTime.plusHours(8))
                                             .build();
        TimePeriod gameTimePeriod3 = TimePeriod.builder()
                                             .start(baseTime.plusHours(9))
                                             .end(baseTime.plusHours(11))
                                             .build();
    
    
        Location gameLocation1 = Location.builder()
                                         .latitude(50.7)
                                         .longitude(4.7).build();
    
        Location gameLocation2 = Location.builder()
                                         .latitude(50.6)
                                         .longitude(4.6).build();
        Location gameLocation3 = Location.builder()
                                         .latitude(56)
                                         .longitude(7).build();
    
    
        Referee referee = RandomDataGenerator.generateReferee().toBuilder()
                                  .availabilityList(availabilityList)
                                  .build();
    
    
        Game game1 = Game.builder().gameLocation(gameLocation1)
                             .gamePeriod(gameTimePeriod1)
                             .amountOfRefereesNeeded(1)
                             .build();
        GameAssignment gameAssignment1 = Game.generateGameAssignments(game1).get(0);
    
        gameAssignment1.setReferee(referee);
        referee.addAssignment(gameAssignment1);
        game1.setAssignments(Game.generateGameAssignments(game1));
    
        Game game2 = Game.builder().gameLocation(gameLocation2)
                             .gamePeriod(gameTimePeriod2)
                             .amountOfRefereesNeeded(1)
                             .build();
        GameAssignment gameAssignment2 = Game.generateGameAssignments(game2).get(0);
    
        gameAssignment2.setReferee(referee);
        referee.addAssignment(gameAssignment2);
        game2.setAssignments(Game.generateGameAssignments(game2));
    
        Game game3 = Game.builder().gameLocation(gameLocation3)
                             .gamePeriod(gameTimePeriod3)
                             .amountOfRefereesNeeded(1)
                             .build();
        GameAssignment gameAssignment3 = Game.generateGameAssignments(game3).get(0);
    
        gameAssignment3.setReferee(referee);
        referee.addAssignment(gameAssignment3);
        game3.setAssignments(Game.generateGameAssignments(game3));
        
        referee.getSortedAssignments().addAll(referee.getAssignments());
        referee.getSortedAssignments().sort(GameAssignmentComparator.COMPARATOR);
        AvailabilityAssignmentMapVariableListener.Pair<Map<Availability, List<GameAssignment>>, List<GameAssignment>> pair = AvailabilityAssignmentMapVariableListener.generateAvailabilityGameAssignmentMap(referee);
        referee.setAvailabilityToGameAssignmentsMap(pair.getLeft());
        referee.setUnassignedAssignments(pair.getRight());
        constraintVerifier.verifyThat(RefereeConstraintProvider::isPhysicallyPossibleConstraint)
                .given(referee)
                .penalizesBy(1);
    }
    /*
    LocalDateTime baseTime = LocalDateTime.now();
        Availability availability1 = Availability.builder().startLocation(Location.builder().latitude(51).longitude(4).build())
                                             .maxRange(100000000)
                                             .availabilityUUID(UUID.randomUUID())
                                             .timePeriod(TimePeriod.builder().start(baseTime).end(baseTime.plusHours(10)).build())
                                             .endLocationEnabled(false)
                                             .build();
        Referee.RefereeBuilder referee = Referee.builder().refereeUUID(UUID.randomUUID())
                                   .isNonExist(false)
                                   .experience(99)
                                                 .availabilityList(Collections)
        Game gameArrivable = Game.builder().gameUUID(UUID.randomUUID())
                                     .gameLocation(Location.builder().latitude(51.1).longitude(4).build())
                                     .gamePeriod(TimePeriod.builder().start(baseTime).end(baseTime.plusHours(1)).build())
                                     .hardMinimumExperience(0)
                                     .priority(1)
                                     .softMaximumExperience(100)
                                     .softMinimumExperience(0)
                                     .build();
        List<GameAssignment> gameArrivableAssignments = Game.generateGameAssignments(gameArrivable);
        referee.assignments(Collections.singletonList(gameArrivableAssignments.get(0)));
        constraintVerifier.verifyThat(RefereeConstraintProvider::isPhysicallyPossibleConstraint)
                .given(referee)
                .penalizesBy(1);
     */
}
