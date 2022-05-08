package me.superbiebel.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import me.superbiebel.referee_planner.domain.TimeTable;
import me.superbiebel.referee_planner.domain.data.TimeTableBuilder;
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
import java.util.concurrent.atomic.AtomicInteger;

@SuppressFBWarnings({"DM_DEFAULT_ENCODING", "DMI_HARDCODED_ABSOLUTE_FILENAME"})
@QuarkusTest
class SolverTests {
    
    @Inject
    SolverManager<TimeTable, Long> solverManager;
    @Inject
    ScoreManager<TimeTable, HardSoftScore> scoreManager;
    
    
    @SuppressFBWarnings({"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", "RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", "RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", "RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", "RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", "RV_RETURN_VALUE_IGNORED_BAD_PRACTICE"})
    @Test
    void solverTest() {
        Assertions.assertDoesNotThrow(() -> {
            TimeTableBuilder timeTableBuilder = new TimeTableBuilder().amountOfGames(550).amountOfReferees(2000);
            
            AtomicInteger solutionCount = new AtomicInteger();
            SolverJob<TimeTable, Long> job = solverManager.solveAndListen(0L, t -> timeTableBuilder.build(), timeTable1 -> {
                try {
                    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                    //Converting the Object to JSONString
                    String jsonString = mapper.writeValueAsString(timeTable1.getGameAssignments());
                    String pathName = "/Users/omegabiebel/Desktop/test/optaplanner_test_referee" + solutionCount.get() + ".json";
                    File f = new File(pathName);
                    f.createNewFile();
                    
                    BufferedWriter writer = new BufferedWriter(new FileWriter(pathName, StandardCharsets.UTF_8));
                    writer.write(jsonString);
                    
                    writer.close();
                    
                    String scoreExplanationPathName = "/Users/omegabiebel/Desktop/test/optaplanner_test_referee" + solutionCount.get() + ".txt";
                    File ScoreExplanationFile = new File(scoreExplanationPathName);
                    ScoreExplanationFile.createNewFile();
                    
                    BufferedWriter scoreExplanationWriter = new BufferedWriter(new FileWriter(scoreExplanationPathName));
                    scoreExplanationWriter.write(String.valueOf(scoreManager.explainScore(timeTable1)));
                    
                    scoreExplanationWriter.close();
                    solutionCount.getAndIncrement();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            TimeTable solution = job.getFinalBestSolution();
            
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            //Converting the Object to JSONString
            String jsonString = mapper.writeValueAsString(solution);
            
            BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/omegabiebel/Desktop/test/optaplanner_test_referee.txt"));
            writer.write(jsonString);
            
            writer.close();
            
            
            System.out.println(scoreManager.explainScore(solution));
            
            Log.info("done solving");
        });
    }
}
