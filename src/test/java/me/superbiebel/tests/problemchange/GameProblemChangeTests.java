package me.superbiebel.tests.problemchange;

import io.quarkus.test.junit.QuarkusTest;
import me.superbiebel.referee_planner.domain.*;
import me.superbiebel.referee_planner.domain.data.RandomDataGenerator;
import me.superbiebel.referee_planner.domain.data.TimeTableGenerator;
import me.superbiebel.referee_planner.problemchanges.game.*;
import org.junit.jupiter.api.*;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolverManager;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class GameProblemChangeTests {
    @Inject
    SolverManager<RefereeTimeTable, Long> solverManager;
    @Inject
    ScoreManager<RefereeTimeTable, HardSoftScore> scoreManager;
    @Test
    @Timeout(120)
    void hardMinimumExperienceChangeTest() throws InterruptedException {
        TimeTableGenerator timeTableGenerator = new TimeTableGenerator().amountOfGames(300).amountOfReferees(900);
        RefereeTimeTable intermediateTimeTable = timeTableGenerator.build();
        int adaptedExperience = 30;
        
        UUID gameUUID = UUID.randomUUID();
        Game game = RandomDataGenerator.generateGame().toBuilder()
                            .gameUUID(gameUUID)
                            .hardMinimumExperience(10)
                            .softMinimumExperience(50)
                            .softMaximumExperience(90)
                            .build();
        List<Game> games = new ArrayList<>(intermediateTimeTable.getGames());
        games.add(game);
        List<GameAssignment> gameAssignments = new ArrayList<>(intermediateTimeTable.getGameAssignments());
        gameAssignments.addAll(RandomDataGenerator.generateGameAssignments(game));
    
        RefereeTimeTable refereeTimeTable = new ProblemChangeSolver().refereeTimeTableProblemChangeSolver(solverManager, scoreManager, "local/solverOutput/hardMinimumExperienceChangeTest",
                GameExperienceChange.builder().gameUUID(gameUUID).newExperience(adaptedExperience).experienceType(GameExperienceChange.EXPERIENCE_TYPE.HARD_MINIMUM).build(),
                intermediateTimeTable.toBuilder()
                        .games(games)
                        .gameAssignments(gameAssignments)
                        .build(), timeTableGenerator);
        Game adaptedGame = refereeTimeTable.getGames().stream().filter(game1 -> game1.getGameUUID().equals(gameUUID)).findFirst().orElseThrow();
        assertEquals(adaptedExperience, adaptedGame.getHardMinimumExperience());
    }
    @Test
    @Timeout(120)
    void softMinimumExperienceChangeTest() throws InterruptedException {
        TimeTableGenerator timeTableGenerator = new TimeTableGenerator().amountOfGames(300).amountOfReferees(900);
        RefereeTimeTable intermediateTimeTable = timeTableGenerator.build();
        int adaptedExperience = 60;
        
        UUID gameUUID = UUID.randomUUID();
        Game game = RandomDataGenerator.generateGame().toBuilder()
                            .gameUUID(gameUUID)
                            .hardMinimumExperience(10)
                            .softMinimumExperience(50)
                            .softMaximumExperience(90)
                            .build();
        List<Game> games = new ArrayList<>(intermediateTimeTable.getGames());
        games.add(game);
        List<GameAssignment> gameAssignments = new ArrayList<>(intermediateTimeTable.getGameAssignments());
        gameAssignments.addAll(RandomDataGenerator.generateGameAssignments(game));
        
        RefereeTimeTable refereeTimeTable = new ProblemChangeSolver().refereeTimeTableProblemChangeSolver(solverManager, scoreManager, "local/solverOutput/softMinimumExperienceChangeTest",
                GameExperienceChange.builder().gameUUID(gameUUID).newExperience(adaptedExperience).experienceType(GameExperienceChange.EXPERIENCE_TYPE.SOFT_MINIMUM).build(),
                intermediateTimeTable.toBuilder()
                        .games(games)
                        .gameAssignments(gameAssignments)
                        .build(), timeTableGenerator);
        Game adaptedGame = refereeTimeTable.getGames().stream().filter(game1 -> game1.getGameUUID().equals(gameUUID)).findFirst().orElseThrow();
        assertEquals(adaptedExperience, adaptedGame.getSoftMinimumExperience());
    }
    @Test
    @Timeout(120)
    void softMaximumExperienceChangeTest() throws InterruptedException {
        TimeTableGenerator timeTableGenerator = new TimeTableGenerator().amountOfGames(300).amountOfReferees(900);
        RefereeTimeTable intermediateTimeTable = timeTableGenerator.build();
        int adaptedExperience = 95;
        
        UUID gameUUID = UUID.randomUUID();
        Game game = RandomDataGenerator.generateGame().toBuilder()
                            .gameUUID(gameUUID)
                            .hardMinimumExperience(10)
                            .softMinimumExperience(50)
                            .softMaximumExperience(90)
                            .build();
        List<Game> games = new ArrayList<>(intermediateTimeTable.getGames());
        games.add(game);
        List<GameAssignment> gameAssignments = new ArrayList<>(intermediateTimeTable.getGameAssignments());
        gameAssignments.addAll(RandomDataGenerator.generateGameAssignments(game));
        
        RefereeTimeTable refereeTimeTable = new ProblemChangeSolver().refereeTimeTableProblemChangeSolver(solverManager, scoreManager, "local/solverOutput/SoftMaximumExperienceChangeTest",
                GameExperienceChange.builder().gameUUID(gameUUID).newExperience(adaptedExperience).experienceType(GameExperienceChange.EXPERIENCE_TYPE.SOFT_MAXIMUM).build(),
                intermediateTimeTable.toBuilder()
                        .games(games)
                        .gameAssignments(gameAssignments)
                        .build(), timeTableGenerator);
        Game adaptedGame = refereeTimeTable.getGames().stream().filter(game1 -> game1.getGameUUID().equals(gameUUID)).findFirst().orElseThrow();
        assertEquals(adaptedExperience, adaptedGame.getSoftMaximumExperience());
    }
    @Test
    @Timeout(120)
    void gameLocationChangeTest() throws InterruptedException {
        TimeTableGenerator timeTableGenerator = new TimeTableGenerator().amountOfGames(300).amountOfReferees(900);
        RefereeTimeTable intermediateTimeTable = timeTableGenerator.build();
        Location adaptedLocation = RandomDataGenerator.giveLocationWithinBelgium();
        
        UUID gameUUID = UUID.randomUUID();
        Game game = RandomDataGenerator.generateGame().toBuilder()
                            .gameUUID(gameUUID)
                            .build();
        List<Game> games = new ArrayList<>(intermediateTimeTable.getGames());
        games.add(game);
        List<GameAssignment> gameAssignments = new ArrayList<>(intermediateTimeTable.getGameAssignments());
        gameAssignments.addAll(RandomDataGenerator.generateGameAssignments(game));
        
        RefereeTimeTable refereeTimeTable = new ProblemChangeSolver().refereeTimeTableProblemChangeSolver(solverManager, scoreManager, "local/solverOutput/SoftMaximumExperienceChangeTest",
                GameLocationChange.builder().newLocation(adaptedLocation).gameUUID(gameUUID).build(),
                intermediateTimeTable.toBuilder()
                        .games(games)
                        .gameAssignments(gameAssignments)
                        .build(), timeTableGenerator);
        Game adaptedGame = refereeTimeTable.getGames().stream().filter(game1 -> game1.getGameUUID().equals(gameUUID)).findFirst().orElseThrow();
        assertEquals(adaptedLocation, adaptedGame.getGameLocation());
    }
    @Test
    @Timeout(120)
    void gamePeriodChangeTest() throws InterruptedException {
        TimeTableGenerator timeTableGenerator = new TimeTableGenerator().amountOfGames(300).amountOfReferees(900);
        RefereeTimeTable intermediateTimeTable = timeTableGenerator.build();
        TimePeriod adaptedTimePeriod = RandomDataGenerator.generateTimePeriodForGame(RandomDataGenerator.BEGIN_DATE);
        
        UUID gameUUID = UUID.randomUUID();
        Game game = RandomDataGenerator.generateGame().toBuilder()
                            .gameUUID(gameUUID)
                            .build();
        List<Game> games = new ArrayList<>(intermediateTimeTable.getGames());
        games.add(game);
        List<GameAssignment> gameAssignments = new ArrayList<>(intermediateTimeTable.getGameAssignments());
        gameAssignments.addAll(RandomDataGenerator.generateGameAssignments(game));
        
        RefereeTimeTable refereeTimeTable = new ProblemChangeSolver().refereeTimeTableProblemChangeSolver(solverManager, scoreManager, "local/solverOutput/SoftMaximumExperienceChangeTest",
                GamePeriodChange.builder().newPeriod(adaptedTimePeriod).gameUUID(gameUUID).build(),
                intermediateTimeTable.toBuilder()
                        .games(games)
                        .gameAssignments(gameAssignments)
                        .build(), timeTableGenerator);
        Game adaptedGame = refereeTimeTable.getGames().stream().filter(game1 -> game1.getGameUUID().equals(gameUUID)).findFirst().orElseThrow();
        assertEquals(adaptedTimePeriod, adaptedGame.getGamePeriod());
    }
    @Test
    @Timeout(120)
    void priorityChangeTest() throws InterruptedException {
        TimeTableGenerator timeTableGenerator = new TimeTableGenerator().amountOfGames(300).amountOfReferees(900);
        RefereeTimeTable intermediateTimeTable = timeTableGenerator.build();
        int adaptedPriority = 95;
        
        UUID gameUUID = UUID.randomUUID();
        Game game = RandomDataGenerator.generateGame().toBuilder()
                            .gameUUID(gameUUID)
                            .build();
        List<Game> games = new ArrayList<>(intermediateTimeTable.getGames());
        games.add(game);
        List<GameAssignment> gameAssignments = new ArrayList<>(intermediateTimeTable.getGameAssignments());
        gameAssignments.addAll(RandomDataGenerator.generateGameAssignments(game));
        
        RefereeTimeTable refereeTimeTable = new ProblemChangeSolver().refereeTimeTableProblemChangeSolver(solverManager, scoreManager, "local/solverOutput/SoftMaximumExperienceChangeTest",
                GamePriorityChange.builder().gameUUID(gameUUID).newPriority(adaptedPriority).build(),
                intermediateTimeTable.toBuilder()
                        .games(games)
                        .gameAssignments(gameAssignments)
                        .build(), timeTableGenerator);
        Game adaptedGame = refereeTimeTable.getGames().stream().filter(game1 -> game1.getGameUUID().equals(gameUUID)).findFirst().orElseThrow();
        assertEquals(adaptedPriority, adaptedGame.getPriority());
    }
    @Test
    @Timeout(120)
    void refereesNeededMoreChangeTest() throws InterruptedException {
        TimeTableGenerator timeTableGenerator = new TimeTableGenerator().amountOfGames(300).amountOfReferees(900);
        RefereeTimeTable intermediateTimeTable = timeTableGenerator.build();
        int adaptedRefereesNeeded = 3;
        
        UUID gameUUID = UUID.randomUUID();
        Game game = RandomDataGenerator.generateGame().toBuilder()
                            .gameUUID(gameUUID)
                            .amountOfRefereesNeeded(2)
                            .build();
        List<Game> games = new ArrayList<>(intermediateTimeTable.getGames());
        games.add(game);
        List<GameAssignment> gameAssignments = new ArrayList<>(intermediateTimeTable.getGameAssignments());
        gameAssignments.addAll(RandomDataGenerator.generateGameAssignments(game));
        
        RefereeTimeTable refereeTimeTable = new ProblemChangeSolver().refereeTimeTableProblemChangeSolver(solverManager, scoreManager, "local/solverOutput/SoftMaximumExperienceChangeTest",
                GameAmountOfRefereesNeededChange.builder().gameUUID(gameUUID).newAmount(adaptedRefereesNeeded).build(),
                intermediateTimeTable.toBuilder()
                        .games(games)
                        .gameAssignments(gameAssignments)
                        .build(), timeTableGenerator);
        Game adaptedGame = refereeTimeTable.getGames().stream().filter(game1 -> game1.getGameUUID().equals(gameUUID)).findFirst().orElseThrow();
        assertEquals(adaptedRefereesNeeded, adaptedGame.getAssignments().size());
        assertEquals(adaptedRefereesNeeded, adaptedGame.getAmountOfRefereesNeeded());
    }
    @Test
    @Timeout(120)
    void refereesNeededLessChangeTest() throws InterruptedException {
        TimeTableGenerator timeTableGenerator = new TimeTableGenerator().amountOfGames(300).amountOfReferees(900);
        RefereeTimeTable intermediateTimeTable = timeTableGenerator.build();
        int adaptedRefereesNeeded = 2;
        
        UUID gameUUID = UUID.randomUUID();
        Game game = RandomDataGenerator.generateGame().toBuilder()
                            .gameUUID(gameUUID)
                            .amountOfRefereesNeeded(3)
                            .build();
        List<Game> games = new ArrayList<>(intermediateTimeTable.getGames());
        games.add(game);
        List<GameAssignment> gameAssignments = new ArrayList<>(intermediateTimeTable.getGameAssignments());
        gameAssignments.addAll(RandomDataGenerator.generateGameAssignments(game));
        
        RefereeTimeTable refereeTimeTable = new ProblemChangeSolver().refereeTimeTableProblemChangeSolver(solverManager, scoreManager, "local/solverOutput/SoftMaximumExperienceChangeTest",
                GameAmountOfRefereesNeededChange.builder().gameUUID(gameUUID).newAmount(adaptedRefereesNeeded).build(),
                intermediateTimeTable.toBuilder()
                        .games(games)
                        .gameAssignments(gameAssignments)
                        .build(), timeTableGenerator);
        Game adaptedGame = refereeTimeTable.getGames().stream().filter(game1 -> game1.getGameUUID().equals(gameUUID)).findFirst().orElseThrow();
        assertEquals(adaptedRefereesNeeded, adaptedGame.getAssignments().size());
        assertEquals(adaptedRefereesNeeded, adaptedGame.getAmountOfRefereesNeeded());
    }
}
