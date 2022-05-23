package me.superbiebel.tests.problemchange;

import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import me.superbiebel.referee_planner.domain.data.RandomDataGenerator;
import me.superbiebel.referee_planner.domain.data.TimeTableGenerator;
import me.superbiebel.referee_planner.domain.data.io.json.JsonOutputConverter;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.sleep;

public class ProblemChangeSolver {
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
}
