package me.superbiebel.referee_planner.domain.data.io.json;

import com.fasterxml.jackson.databind.JsonNode;
import me.superbiebel.referee_planner.domain.Game;
import me.superbiebel.referee_planner.domain.GameAssignment;
import me.superbiebel.referee_planner.domain.Referee;
import me.superbiebel.referee_planner.domain.TimeTable;

import java.util.ArrayList;
import java.util.List;

public class JsonInputConverter {
    
    public static TimeTable generateTimeTableFromJson(JsonNode timeTableNode) {
        List<Game> gameList = new ArrayList<>();
        timeTableNode.get("games").elements().forEachRemaining(gameNode -> gameList.add(generateGame(gameNode)));
        
        List<Referee> refereeList = new ArrayList<>();
        
        
        List<GameAssignment> gameAssignmentList = new ArrayList<>();
        return null;
    }
    
    public static Game generateGame(JsonNode gameNode) {
        return null;
    }
    
    public static Game generateLocation(JsonNode locationNode) {
        return null;
    }
    
    public static Game generateTimePeriod(JsonNode timePeriodNode) {
        return null;
    }
    
    public static Game generateReferee(JsonNode gameNode, Referee game) {
        return null;
    }
    
    public static GameAssignment generateGameAssignment(JsonNode gameAssignmentNode) {
        return null;
    }
    
    private JsonInputConverter() {
    }
}
