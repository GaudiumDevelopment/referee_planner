package me.superbiebel.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import me.superbiebel.referee_planner.domain.TimeTable;
import me.superbiebel.referee_planner.domain.datagenerator.TimeTableBuilder;
import org.junit.jupiter.api.*;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.atomic.AtomicInteger;

@QuarkusTest
class SolverTests {
    
    @Inject
    SolverManager<TimeTable, Long> solverManager;
    @Inject
    ScoreManager<TimeTable, HardSoftScore> scoreManager;
    
    
    @Test
    void solverTest() {
        Assertions.assertDoesNotThrow(() -> {
            TimeTableBuilder timeTableBuilder = new TimeTableBuilder().amountOfGames(10).amountOfReferees(30);
    
            AtomicInteger solutionCount = new AtomicInteger();
            SolverJob<TimeTable, Long> job = solverManager.solveAndListen(0L, t -> timeTableBuilder.build(), timeTable1 -> {
                try {
                    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                    //Converting the Object to JSONString
                    String jsonString = mapper.writeValueAsString(timeTable1);
                    String pathName = "/Users/omegabiebel/Desktop/test/optaplanner_test_referee" + solutionCount.get() + ".txt";
                    File f = new File(pathName);
                    f.createNewFile();
            
                    BufferedWriter writer = new BufferedWriter(new FileWriter(pathName));
                    writer.write(jsonString);
            
                    writer.close();
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
