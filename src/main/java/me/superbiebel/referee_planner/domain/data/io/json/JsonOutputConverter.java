package me.superbiebel.referee_planner.domain.data.io.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.superbiebel.referee_planner.domain.*;

public class JsonOutputConverter {
    
    public static JsonNode timeTableToJson(TimeTable timeTable) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode timeTableNode = mapper.createObjectNode();
        ArrayNode gameNode = mapper.createArrayNode();
        timeTable.getGames().forEach(game -> gameNode.add(generateGameObjectNode(mapper, game)));
        timeTableNode.set("games", gameNode);
        
        ArrayNode refereeNode = mapper.createArrayNode();
        timeTable.getReferees().forEach(referee -> refereeNode.add(generateRefereeObjectNode(mapper, referee)));
        timeTableNode.set("referees", refereeNode);
        
        ArrayNode gameAssignmentNode = mapper.createArrayNode();
        timeTable.getGameAssignments().forEach(gameAssignment -> gameAssignmentNode.add(generateGameAssignmentObjectNode(mapper, gameAssignment)));
        timeTableNode.set("gameAssignments", refereeNode);
        return timeTableNode;
    }
    
    public static ObjectNode generateGameObjectNode(ObjectMapper mapper, Game game) {
        ObjectNode gameNode = mapper.createObjectNode();
        gameNode.put("gameUUID", game.getGameUUID().toString());
        gameNode.set("gameLocation", generateLocationObjectNode(mapper, game.getGameLocation()));
        gameNode.set("gameRefereePeriod", generateTimePeriodObjectNode(mapper, game.getGameRefereePeriod()));
        gameNode.put("amountOfRefereesNeeded", game.getAmountOfRefereesNeeded());
        gameNode.put("hardMinimumExperience", game.getHardMinimumExperience());
        gameNode.put("softMinimumExperience", game.getSoftMinimumExperience());
        gameNode.put("softMaximumExperience", game.getSoftMaximumExperience());
        return gameNode;
    }
    
    public static ObjectNode generateLocationObjectNode(ObjectMapper mapper, Location location) {
        ObjectNode locationNode = mapper.createObjectNode();
        locationNode.put("latitude", location.getLatitude());
        locationNode.put("longitude", location.getLongitude());
        return locationNode;
    }
    
    public static ObjectNode generateTimePeriodObjectNode(ObjectMapper mapper, TimePeriod timePeriod) {
        ObjectNode timePeriodNode = mapper.createObjectNode();
        timePeriodNode.put("start", timePeriod.getStart().toString());
        timePeriodNode.put("end", timePeriod.getEnd().toString());
        return timePeriodNode;
    }
    
    public static ObjectNode generateRefereeObjectNode(ObjectMapper mapper, Referee referee) {
        ObjectNode refereeNode = mapper.createObjectNode();
        refereeNode.put("isNonExist", referee.isNonExist());
        refereeNode.put("refereeUUID", referee.getRefereeUUID().toString());
        refereeNode.put("experience", referee.getExperience());
        refereeNode.set("homeLocation", generateLocationObjectNode(mapper, referee.getHomeLocation()));
        ArrayNode availabilityNode = mapper.createArrayNode();
        referee.getAvailabilityList().forEach(timePeriod -> availabilityNode.add(generateTimePeriodObjectNode(mapper, timePeriod)));
        refereeNode.set("availability", availabilityNode);
        return refereeNode;
    }
    
    public static ObjectNode generateGameAssignmentObjectNode(ObjectMapper mapper, GameAssignment gameAssignment) {
        ObjectNode gameAssignmentNode = mapper.createObjectNode();
        gameAssignmentNode.put("gameUUID", gameAssignment.getGame().getGameUUID().toString());
        gameAssignmentNode.put("refereeUUID", gameAssignment.getReferee().getRefereeUUID().toString());
        gameAssignmentNode.put("indexInGame", gameAssignment.getIndexInGame());
        return gameAssignmentNode;
    }
    
    private JsonOutputConverter() {
    }
}
