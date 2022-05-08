package me.superbiebel.tests;

import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import me.superbiebel.referee_planner.RefereeConstraintProvider;
import me.superbiebel.referee_planner.domain.*;
import me.superbiebel.referee_planner.domain.datagenerator.RandomDataGenerator;
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
                                                .game(Game.builder().hardMinimumExperience(10).gamePeriod(RandomDataGenerator.generateTimePeriod()).build())
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
                                                .game(Game.builder().softMinimumExperience(10).gamePeriod(RandomDataGenerator.generateTimePeriod()).build())
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
                                                .game(Game.builder().softMaximumExperience(10).gamePeriod(RandomDataGenerator.generateTimePeriod()).build())
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
        
        GameAssignment gameAssignment1 = GameAssignment.builder().game(Game.builder().gameLocation(location1).gamePeriod(TimePeriod.builder().start(LocalDateTime.now().minusDays(4)).build()).build()).build();
        GameAssignment gameAssignment2 = GameAssignment.builder().game(Game.builder().gameLocation(location2).gamePeriod(TimePeriod.builder().start(LocalDateTime.now().minusDays(3)).build()).build()).build();
        GameAssignment gameAssignment3 = GameAssignment.builder().game(Game.builder().gameLocation(location3).gamePeriod(TimePeriod.builder().start(LocalDateTime.now().minusDays(2)).build()).build()).build();
        GameAssignment gameAssignment4 = GameAssignment.builder().game(Game.builder().gameLocation(location4).gamePeriod(TimePeriod.builder().start(LocalDateTime.now().minusDays(1)).build()).build()).build();
        
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
                                                 .referee(Referee.builder().isNonExist(true).build())
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
                .penalizesBy(1);
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
    
}
