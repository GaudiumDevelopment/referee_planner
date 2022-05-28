package me.superbiebel.tests.json;

import com.github.javaparser.utils.Log;
import io.quarkus.test.junit.QuarkusTest;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import me.superbiebel.referee_planner.domain.data.TimeTableGenerator;
import me.superbiebel.referee_planner.domain.data.io.json.JsonDatasetConverter;
import me.superbiebel.referee_planner.domain.data.io.json.JsonOutputConverter;
import org.junit.jupiter.api.*;

@QuarkusTest
class JsonConverterTests {
    
    @Test
    void outInTest() {
        RefereeTimeTable solution = new TimeTableGenerator().amountOfGames(1).amountOfReferees(1).build();
        RefereeTimeTable convertedSolution = JsonDatasetConverter.generateTimeTableFromJson(JsonOutputConverter.refereeTimeTableToJson(solution), true);
        Assertions.assertEquals(solution, convertedSolution);
        Log.info("done");
    }
}
