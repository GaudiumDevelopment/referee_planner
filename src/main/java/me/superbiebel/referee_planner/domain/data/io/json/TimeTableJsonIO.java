package me.superbiebel.referee_planner.domain.data.io.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

import java.io.File;
import java.io.IOException;

public class TimeTableJsonIO implements SolutionFileIO<RefereeTimeTable> {
    
    @Override
    public RefereeTimeTable read(File inputSolutionFile) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode masterJSON;
        try {
            masterJSON = mapper.readTree(inputSolutionFile);
            return JsonDatasetConverter.generateTimeTableFromJson(masterJSON, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void write(RefereeTimeTable refereeTimeTable, File outputSolutionFile) {
        JsonOutputConverter.refereeTimeTableToJson(refereeTimeTable);
    }
    
    @Override
    public String getInputFileExtension() {
        return "json";
    }
}
