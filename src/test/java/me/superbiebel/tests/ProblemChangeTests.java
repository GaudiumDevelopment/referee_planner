package me.superbiebel.tests;

import io.quarkus.test.junit.QuarkusTest;
import me.superbiebel.referee_planner.domain.*;
import me.superbiebel.referee_planner.domain.data.RandomDataGenerator;
import me.superbiebel.referee_planner.domain.data.TimeTableGenerator;
import me.superbiebel.referee_planner.domain.data.io.json.JsonOutputConverter;
import me.superbiebel.referee_planner.problemchanges.game.GameExperienceChange;
import me.superbiebel.referee_planner.problemchanges.referee.RefereeAvailabilityChange;
import me.superbiebel.referee_planner.problemchanges.referee.RefereeExperienceChange;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.change.ProblemChange;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ProblemChangeTests {
    @Inject
    SolverManager<RefereeTimeTable, Long> solverManager;
    @Inject
    ScoreManager<RefereeTimeTable, HardSoftScore> scoreManager;
    
    public RefereeTimeTable refereeTimeTableProblemChangeSolver(String output, ProblemChange<RefereeTimeTable> change, RefereeTimeTable input, TimeTableGenerator timeTableGenerator) throws InterruptedException {
        long PROBLEM_ID = RandomDataGenerator.generateLongInRange(0, 1000, true);
        
        String outputPath = output + "/" + LocalDateTime.now() + "/";
        File outputDir = new File(outputPath);
        outputDir.mkdirs();
        
        
        CountDownLatch latch = new CountDownLatch(1);
        
        AtomicInteger solutionCount = new AtomicInteger();
        AtomicReference<RefereeTimeTable> currentBestSolution = new AtomicReference<>();
        SolverJob<RefereeTimeTable, Long> job = solverManager.solveAndListen(PROBLEM_ID, t -> timeTableGenerator.build()//please don't mind the workaround
                                                                                              .toBuilder()
                                                                                              .referees(input.getReferees())
                                                                                              .games(input.getGames())
                                                                                              .gameAssignments(input.getGameAssignments())
                                                                                              .build(), timeTable1 -> {
            try {
                currentBestSolution.set(timeTable1);
                String jsonString = JsonOutputConverter.refereeTimeTableToJson(timeTable1).toPrettyString();
                String pathName = outputPath + "solution-" + solutionCount.get() + ".json";
                File outputFile = new File(pathName);
                outputFile.createNewFile();
                
                BufferedWriter writer = new BufferedWriter(new FileWriter(pathName, StandardCharsets.UTF_8));
                writer.write(jsonString);
                
                writer.close();
                
                String scoreExplanationPathName = outputPath + "explanation-" + solutionCount.get() + ".txt";
                File scoreExplanationFile = new File(scoreExplanationPathName);
                scoreExplanationFile.createNewFile();
                
                BufferedWriter scoreExplanationWriter = new BufferedWriter(new FileWriter(scoreExplanationPathName));
                scoreExplanationWriter.write(String.valueOf(scoreManager.explainScore(timeTable1)));
                
                scoreExplanationWriter.close();
                solutionCount.getAndIncrement();
                latch.countDown();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        latch.await();
        sleep(1000);
        solverManager.addProblemChange(PROBLEM_ID, change);
        sleep(20000);
        job.terminateEarly();
        return currentBestSolution.get();
    }
    
    @Test
    @Execution(ExecutionMode.CONCURRENT)
    @Timeout(120)
    void availabilityChangeTest() throws InterruptedException {
        TimeTableGenerator timeTableGenerator = new TimeTableGenerator().amountOfGames(300).amountOfReferees(900);
        RefereeTimeTable intermediateTimeTable = timeTableGenerator.build();
        List<Referee> referees = new ArrayList<>(intermediateTimeTable.getReferees());
        
        UUID oldAvailabilityUUID = UUID.randomUUID();
        Availability oldAvailability = RandomDataGenerator
                                               .generateAvailabilityListForReferee(1)
                                               .get(0)
                                               .toBuilder()
                                               .availabilityUUID(oldAvailabilityUUID)
                                               .startLocation(RandomDataGenerator.giveLocationWithinBelgium())
                                               .build();
        UUID newAvailabilityUUID = UUID.randomUUID();
        Availability newAvailability = RandomDataGenerator
                                               .generateAvailabilityListForReferee(1)
                                               .get(0)
                                               .toBuilder()
                                               .availabilityUUID(newAvailabilityUUID)
                                               .startLocation(RandomDataGenerator.giveLocationWithinBelgium())
                                               .build();
        List<Availability> availabilityList = new ArrayList<>();
        availabilityList.add(oldAvailability);
        UUID refereeUUID = UUID.randomUUID();
        Referee referee = RandomDataGenerator.generateReferee()
                                  .toBuilder()
                                  .refereeUUID(refereeUUID)
                                  .availabilityList(availabilityList)
                                  .build();
        referees.add(referee);
        
        RefereeTimeTable refereeTimeTable = refereeTimeTableProblemChangeSolver("local/solverOutput/availabilityProblemChangeTests",
                RefereeAvailabilityChange.builder()
                        .newAvailability(newAvailability)
                        .oldAvailabilityUUID(oldAvailabilityUUID)
                        .refereeUUID(refereeUUID).build(),
                intermediateTimeTable.toBuilder()
                        .referees(referees)
                        .build(), timeTableGenerator);
        Referee adaptedReferee = refereeTimeTable.getReferees().stream().filter(referee1 -> referee1.getRefereeUUID().equals(refereeUUID)).findFirst().get();
        assertEquals(newAvailabilityUUID, adaptedReferee.getAvailabilityList().get(0).getAvailabilityUUID());
    }
    @Test
    @Execution(ExecutionMode.CONCURRENT)
    @Timeout(120)
    void availabilityRemoveTest() throws InterruptedException {
        TimeTableGenerator timeTableGenerator = new TimeTableGenerator().amountOfGames(300).amountOfReferees(900);
        RefereeTimeTable intermediateTimeTable = timeTableGenerator.build();
        List<Referee> referees = new ArrayList<>(intermediateTimeTable.getReferees());
        
        UUID oldAvailabilityUUID = UUID.randomUUID();
        Availability oldAvailability = RandomDataGenerator
                                               .generateAvailabilityListForReferee(1)
                                               .get(0)
                                               .toBuilder()
                                               .availabilityUUID(oldAvailabilityUUID)
                                               .startLocation(RandomDataGenerator.giveLocationWithinBelgium())
                                               .build();
        UUID newAvailabilityUUID = UUID.randomUUID();
        Availability newAvailability = RandomDataGenerator
                                               .generateAvailabilityListForReferee(1)
                                               .get(0)
                                               .toBuilder()
                                               .availabilityUUID(newAvailabilityUUID)
                                               .startLocation(RandomDataGenerator.giveLocationWithinBelgium())
                                               .build();
        List<Availability> availabilityList = new ArrayList<>();
        availabilityList.add(oldAvailability);
        UUID refereeUUID = UUID.randomUUID();
        Referee referee = RandomDataGenerator.generateReferee()
                                  .toBuilder()
                                  .refereeUUID(refereeUUID)
                                  .availabilityList(availabilityList)
                                  .build();
        referees.add(referee);
        
        RefereeTimeTable refereeTimeTable = refereeTimeTableProblemChangeSolver("local/solverOutput/availabilityProblemChangeTests",
                RefereeAvailabilityChange.builder()
                        .newAvailability(newAvailability)
                        .oldAvailabilityUUID(oldAvailabilityUUID)
                        .refereeUUID(refereeUUID).build(),
                intermediateTimeTable.toBuilder()
                        .referees(referees)
                        .build(), timeTableGenerator);
        Referee adaptedReferee = refereeTimeTable.getReferees().stream().filter(referee1 -> referee1.getRefereeUUID().equals(refereeUUID)).findFirst().get();
        assertEquals(newAvailabilityUUID, adaptedReferee.getAvailabilityList().get(0).getAvailabilityUUID());
    }
    @Test
    @Execution(ExecutionMode.CONCURRENT)
    @Timeout(120)
    void experienceChangeTest() throws InterruptedException {
        TimeTableGenerator timeTableGenerator = new TimeTableGenerator().amountOfGames(300).amountOfReferees(900);
        RefereeTimeTable intermediateTimeTable = timeTableGenerator.build();
        List<Referee> referees = new ArrayList<>(intermediateTimeTable.getReferees());
        int adaptedExperience = 90;
        UUID refereeUUID = UUID.randomUUID();
        Referee referee = RandomDataGenerator.generateReferee()
                                  .toBuilder()
                                  .refereeUUID(refereeUUID)
                                  .build();
        referees.add(referee);
        RefereeTimeTable refereeTimeTable = refereeTimeTableProblemChangeSolver("local/solverOutput/availabilityProblemChangeTests",
                RefereeExperienceChange.builder().refereeUUID(refereeUUID).newExperience(adaptedExperience).build(),
                intermediateTimeTable.toBuilder()
                        .referees(referees)
                        .build(), timeTableGenerator);
        Referee adaptedReferee = refereeTimeTable.getReferees().stream().filter(referee1 -> referee1.getRefereeUUID().equals(refereeUUID)).findFirst().get();
        assertEquals(adaptedExperience, adaptedReferee.getExperience());
    }
    @Test
    @Execution(ExecutionMode.CONCURRENT)
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
    
        RefereeTimeTable refereeTimeTable = refereeTimeTableProblemChangeSolver("local/solverOutput/hardMinimumExperienceChangeTest",
                GameExperienceChange.builder().gameUUID(gameUUID).newExperience(adaptedExperience).experienceType(GameExperienceChange.EXPERIENCE_TYPE.HARD_MINIMUM).build(),
                intermediateTimeTable.toBuilder()
                        .games(games)
                        .gameAssignments(gameAssignments)
                        .build(), timeTableGenerator);
        Game adaptedGame = refereeTimeTable.getGames().stream().filter(game1 -> game1.getGameUUID().equals(gameUUID)).findFirst().get();
        assertEquals(adaptedExperience, adaptedGame.getHardMinimumExperience());
    }
    @Test
    @Execution(ExecutionMode.CONCURRENT)
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
        
        RefereeTimeTable refereeTimeTable = refereeTimeTableProblemChangeSolver("local/solverOutput/softMinimumExperienceChangeTest",
                GameExperienceChange.builder().gameUUID(gameUUID).newExperience(adaptedExperience).experienceType(GameExperienceChange.EXPERIENCE_TYPE.SOFT_MINIMUM).build(),
                intermediateTimeTable.toBuilder()
                        .games(games)
                        .gameAssignments(gameAssignments)
                        .build(), timeTableGenerator);
        Game adaptedGame = refereeTimeTable.getGames().stream().filter(game1 -> game1.getGameUUID().equals(gameUUID)).findFirst().get();
        assertEquals(adaptedExperience, adaptedGame.getSoftMinimumExperience());
    }
    @Test
    @Execution(ExecutionMode.CONCURRENT)
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
        
        RefereeTimeTable refereeTimeTable = refereeTimeTableProblemChangeSolver("local/solverOutput/SoftMaximumExperienceChangeTest",
                GameExperienceChange.builder().gameUUID(gameUUID).newExperience(adaptedExperience).experienceType(GameExperienceChange.EXPERIENCE_TYPE.SOFT_MAXIMUM).build(),
                intermediateTimeTable.toBuilder()
                        .games(games)
                        .gameAssignments(gameAssignments)
                        .build(), timeTableGenerator);
        Game adaptedGame = refereeTimeTable.getGames().stream().filter(game1 -> game1.getGameUUID().equals(gameUUID)).findFirst().get();
        assertEquals(adaptedExperience, adaptedGame.getSoftMaximumExperience());
    }
}
