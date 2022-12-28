package me.superbiebel.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import me.superbiebel.referee_planner.domain.data.TimeTableGenerator;
import me.superbiebel.referee_planner.domain.data.io.json.JsonOutputConverter;
import org.junit.jupiter.api.*;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@QuarkusTest
class SolverTests {
    
    @Inject
    SolverManager<RefereeTimeTable, Long> solverManager;
    @Inject
    ScoreManager<RefereeTimeTable, HardSoftScore> scoreManager;
    
    @Test
    @Disabled("This test will go on endlessly, this is purely to test algorithms and stuff")
    void solverTest() {
        Assertions.assertDoesNotThrow(() -> {
            TimeTableGenerator timeTableGenerator = new TimeTableGenerator().amountOfGames(300).amountOfReferees(900);
    
            String outputPath = "local" + System.getProperty("file.separator") + "solverOutput" + System.getProperty("file.separator") + "solverTests"+ System.getProperty("file.separator") + LocalDateTime.now().toString()
                                                                                                                                                                                                       .replace("-", "_")
                                                                                                                                                                                                       .replace(":", "_")
                                                                                                                                                                                                       .replace(".", "_");// + System.getProperty("file.separator");
            File outputDir = new File(outputPath);
            outputDir.mkdirs();
    
            AtomicInteger solutionCount = new AtomicInteger();
            SolverJob<RefereeTimeTable, Long> job = solverManager.solveAndListen(0L, t -> timeTableGenerator.build(), timeTable1 -> {
                try {
                    String jsonString = JsonOutputConverter.refereeTimeTableToJson(timeTable1).toPrettyString();
                    String pathName = outputPath + System.getProperty("file.separator") + "solution_" + solutionCount.get() + ".json";
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
            
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            RefereeTimeTable solution = job.getFinalBestSolution();
    
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            //Converting the Object to JSONString
            String jsonString = JsonOutputConverter.refereeTimeTableToJson(solution).toPrettyString();
            /*
            BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("file.separator") + "Users" + System.getProperty("file.separator") + "omegabiebel" + System.getProperty("file.separator") + "Desktop" + System.getProperty("file.separator") + "test" + System.getProperty("file.separator") + "optaplanner_test_referee.json"));
            writer.write(jsonString);
    
            writer.close();
            */
    
    
            System.out.println(scoreManager.explainScore(solution));
    
            Log.info("done solving");
        });
    }
}
