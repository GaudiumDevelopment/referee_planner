package me.superbiebel.tests;

import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import me.superbiebel.referee_planner.RefereeConstraintProvider;
import me.superbiebel.referee_planner.domain.*;
import me.superbiebel.referee_planner.domain.data.RandomDataGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@QuarkusTest
class ConstraintProviderTests {
    
    @Inject
    ConstraintVerifier<RefereeConstraintProvider, TimeTable> constraintVerifier;
    
    @Test
    void hardMinimumExperienceTest() {
        Referee referee = Referee.builder().experience(0).build();
        GameAssignment gameAssignment = GameAssignment.builder()
                                                .game(Game.builder().hardMinimumExperience(10).gameRefereePeriod(RandomDataGenerator.generateTimePeriodForGame()).build())
                                                .referee(referee).build();
        referee.addAssignment(gameAssignment);
        
        constraintVerifier.verifyThat(RefereeConstraintProvider::sufficientHardMinimumExperienceLevel)
                .given(referee, gameAssignment)
                .penalizesBy(10);
    }
    @Test
    void softMinimumExperienceTest() {
        Referee referee = Referee.builder().experience(0).build();
        GameAssignment gameAssignment = GameAssignment.builder()
                                                .game(Game.builder().softMinimumExperience(10).gameRefereePeriod(RandomDataGenerator.generateTimePeriodForGame()).build())
                                                .referee(referee).build();
        referee.addAssignment(gameAssignment);
        
        constraintVerifier.verifyThat(RefereeConstraintProvider::sufficientSoftMinimumExperienceLevel)
                .given(referee, gameAssignment)
                .penalizesBy(10);
    }
    @Execution(ExecutionMode.CONCURRENT)
    @Test
    void softMaximumExperienceTest() {
        Referee referee = Referee.builder().experience(20).build();
        GameAssignment gameAssignment = GameAssignment.builder()
                                                .game(Game.builder().softMaximumExperience(10).gameRefereePeriod(RandomDataGenerator.generateTimePeriodForGame()).build())
                                                .referee(referee).build();
        referee.addAssignment(gameAssignment);
        
        constraintVerifier.verifyThat(RefereeConstraintProvider::sufficientSoftMaximumExperienceLevel)
                .given(referee, gameAssignment)
                .penalizesBy(10);
    }
    @Execution(ExecutionMode.CONCURRENT)
    @Test
    void distanceSoftConstraintTest() {
        Location homeLocation = Location.builder()
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
    
        Referee referee = Referee.builder().homeLocation(homeLocation).build();
    
        GameAssignment gameAssignment1 = GameAssignment.builder().game(Game.builder().gameLocation(location1).gameRefereePeriod(TimePeriod.builder().start(LocalDateTime.now().minusDays(4)).build()).build()).build();
        GameAssignment gameAssignment2 = GameAssignment.builder().game(Game.builder().gameLocation(location2).gameRefereePeriod(TimePeriod.builder().start(LocalDateTime.now().minusDays(3)).build()).build()).build();
        GameAssignment gameAssignment3 = GameAssignment.builder().game(Game.builder().gameLocation(location3).gameRefereePeriod(TimePeriod.builder().start(LocalDateTime.now().minusDays(2)).build()).build()).build();
        GameAssignment gameAssignment4 = GameAssignment.builder().game(Game.builder().gameLocation(location4).gameRefereePeriod(TimePeriod.builder().start(LocalDateTime.now().minusDays(1)).build()).build()).build();
    
        referee.addAssignment(gameAssignment1);
        referee.addAssignment(gameAssignment2);
        referee.addAssignment(gameAssignment3);
        referee.addAssignment(gameAssignment4);
    
        Log.debug("homelocation to location 1: " + homeLocation.getDistanceTo(location1));
        Log.debug("location 1 to location 2: " + location1.getDistanceTo(location2));
        Log.debug("location 2 to location 3: " + location2.getDistanceTo(location3));
        Log.debug("location 3 to location 4: " + location3.getDistanceTo(location4));
        
        
        constraintVerifier.verifyThat(RefereeConstraintProvider::distanceSoftConstraint)
                .given(referee).penalizesBy(homeLocation.getDistanceTo(location1) + location1.getDistanceTo(location2) + location2.getDistanceTo(location3) + location3.getDistanceTo(location4));
    }
    
    @Test
    @Execution(ExecutionMode.CONCURRENT)
    void notEnoughRefereesConstraintTest() {
        Game game = Game.builder().amountOfRefereesNeeded(3).build();
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
                                                 .referee(Referee.builder().isNonExist(true).build())
                                                 .build();
        List<GameAssignment> gameAssignmentList = new ArrayList<>();
        gameAssignmentList.add(gameAssignment0);
        gameAssignmentList.add(gameAssignment1);
        gameAssignmentList.add(gameAssignment2);
        game.setAssignments(gameAssignmentList);
    
        constraintVerifier.verifyThat(RefereeConstraintProvider::notEnoughRefereesConstraint)
                .given(game)
                .penalizesBy(0);
    }
    
    @Test
    @Execution(ExecutionMode.CONCURRENT)
    void tooManyRefereesConstraintTest() {
        Game game = Game.builder().amountOfRefereesNeeded(1).build();
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
    @Execution(ExecutionMode.CONCURRENT)
    void firstRefereeIsNotNonExistingConstraintTest() {
        Game game = Game.builder().amountOfRefereesNeeded(3).build();
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
        
        constraintVerifier.verifyThat(RefereeConstraintProvider::firstRefereeIsNotNonExistingConstraint)
                .given(gameAssignment0, gameAssignment1, gameAssignment2)
                .penalizesBy(1);
    }
    
    @Test
    @Execution(ExecutionMode.CONCURRENT)
    void sameRefereeMultipleGameIndexConstraintTest() {
        Game game = Game.builder().amountOfRefereesNeeded(3).build();
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
    @Execution(ExecutionMode.CONCURRENT)
    void isInAvailabilityConstraint() {
        List<TimePeriod> availabilityList = new ArrayList<>();
        
        LocalDateTime baseTime = LocalDateTime.now();
        
        TimePeriod timePeriod1 = TimePeriod.builder()
                                         .start(baseTime)
                                         .end(baseTime.plusHours(10))
                                         .build();
        TimePeriod timePeriod2 = TimePeriod.builder()
                                         .start(baseTime.plusHours(12))
                                         .end(baseTime.plusHours(22))
                                         .build();
        TimePeriod timePeriod3 = TimePeriod.builder()
                                         .start(baseTime.plusHours(26))
                                         .end(baseTime.plusHours(34))
                                         .build();
        availabilityList.add(timePeriod1);
        availabilityList.add(timePeriod2);
        availabilityList.add(timePeriod3);
        
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
        
        Location homeLocation = Location.builder()
                                        .latitude(50.92729190520198)
                                        .longitude(4.761309967148309).build();
        Location gameLocation1 = Location.builder()
                                         .latitude(50.7)
                                         .longitude(4.7).build();
        
        Location gameLocation2 = Location.builder()
                                         .latitude(50.6)
                                         .longitude(4.6).build();
        Location gameLocation3 = Location.builder()
                                         .latitude(55.92729190520198)
                                         .longitude(6.761309967148309).build();
        
        
        Referee referee = Referee.builder().homeLocation(homeLocation)
                                  .availabilityList(availabilityList)
                                  .build();
        
        
        Game game1 = Game.builder().gameLocation(gameLocation1)
                             .gameRefereePeriod(gameTimePeriod1)
                             .amountOfRefereesNeeded(1)
                             .build();
        GameAssignment gameAssignment1 = RandomDataGenerator.generateGameAssignment(game1).get(0);
        
        gameAssignment1.setReferee(referee);
        referee.addAssignment(gameAssignment1);
        game1.setAssignments(RandomDataGenerator.generateGameAssignment(game1));
        
        Game game2 = Game.builder().gameLocation(gameLocation2)
                             .gameRefereePeriod(gameTimePeriod2)
                             .amountOfRefereesNeeded(1)
                             .build();
        GameAssignment gameAssignment2 = RandomDataGenerator.generateGameAssignment(game2).get(0);
        
        gameAssignment2.setReferee(referee);
        referee.addAssignment(gameAssignment2);
        game2.setAssignments(RandomDataGenerator.generateGameAssignment(game2));
        
        Game game3 = Game.builder().gameLocation(gameLocation3)
                             .gameRefereePeriod(gameTimePeriod3)
                             .amountOfRefereesNeeded(1)
                             .build();
        GameAssignment gameAssignment3 = RandomDataGenerator.generateGameAssignment(game3).get(0);
        
        gameAssignment3.setReferee(referee);
        referee.addAssignment(gameAssignment3);
        game3.setAssignments(RandomDataGenerator.generateGameAssignment(game3));
        
        
        constraintVerifier.verifyThat(RefereeConstraintProvider::isInAvailabilityConstraint)
                .given(referee)
                .penalizesBy(1);
    }
}
