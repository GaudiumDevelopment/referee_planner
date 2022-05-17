package me.superbiebel.referee_planner.domain.data.io.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.superbiebel.referee_planner.domain.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class JsonOutputConverter {
    
    public static JsonNode refereeTimeTableToJson(RefereeTimeTable refereeTimeTable) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode timeTableNode = mapper.createObjectNode();
        ArrayNode gameNode = mapper.createArrayNode();
        refereeTimeTable.getGames().forEach(game -> gameNode.add(generateGameObjectNode(mapper, game)));
        timeTableNode.set("games", gameNode);
        
        ArrayNode refereeNode = mapper.createArrayNode();
        refereeTimeTable.getReferees().forEach(referee -> refereeNode.add(generateRefereeObjectNode(mapper, referee)));
        timeTableNode.set("referees", refereeNode);
        
        ArrayNode gameAssignmentNode = mapper.createArrayNode();
        refereeTimeTable.getGameAssignments().forEach(gameAssignment -> gameAssignmentNode.add(generateGameAssignmentObjectNode(mapper, gameAssignment)));
        timeTableNode.set("gameAssignments", gameAssignmentNode);
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
        gameNode.put("priority", game.getPriority());
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
        timePeriodNode.set("start", generateLocalDateTimeObjectNode(mapper, timePeriod.getStart()));
        timePeriodNode.set("end", generateLocalDateTimeObjectNode(mapper, timePeriod.getEnd()));
        return timePeriodNode;
    }
    
    public static ObjectNode generateLocalDateTimeObjectNode(ObjectMapper mapper, LocalDateTime localDateTime) {
        ObjectNode dateTimeNode = mapper.createObjectNode();
        dateTimeNode.put("year", localDateTime.getYear());
        dateTimeNode.put("month", localDateTime.getMonthValue());
        dateTimeNode.put("day", localDateTime.getDayOfMonth());
        dateTimeNode.put("hour", localDateTime.getHour());
        dateTimeNode.put("minute", localDateTime.getMinute());
        return dateTimeNode;
    }
    
    public static ObjectNode generateRefereeObjectNode(ObjectMapper mapper, Referee referee) {
        ObjectNode refereeNode = mapper.createObjectNode();
        refereeNode.put("isNonExist", referee.isNonExist());
        if (!referee.isNonExist()) {
            refereeNode.put("refereeUUID", referee.getRefereeUUID().toString());
            refereeNode.put("experience", referee.getExperience());
            ArrayNode availabilityNode = mapper.createArrayNode();
            referee.getAvailabilityList().forEach(timePeriod -> availabilityNode.add(generateAvailabilityNode(mapper, timePeriod)));
            refereeNode.set("availability", availabilityNode);
        }
        return refereeNode;
    }
    
    public static ObjectNode generateAvailabilityNode(ObjectMapper mapper, Availability availability) {
        ObjectNode availabilityNode = mapper.createObjectNode();
        availabilityNode.set("startLocation", generateLocationObjectNode(mapper, availability.getStartLocation()));
        availabilityNode.put("endLocationEnabled", availability.isEndLocationEnabled());
        if (availability.isEndLocationEnabled()) {
            availabilityNode.set("endLocation", generateLocationObjectNode(mapper, availability.getEndLocation()));
        }
        
        return availabilityNode;
    }
    
    public static ObjectNode generateGameAssignmentObjectNode(ObjectMapper mapper, GameAssignment gameAssignment) {
        ObjectNode gameAssignmentNode = mapper.createObjectNode();
        gameAssignmentNode.put("gameUUID", gameAssignment.getGame().getGameUUID().toString());
        if (gameAssignment.getReferee() != null) {
            UUID refereeUUID = gameAssignment.getReferee().getRefereeUUID() != null ? gameAssignment.getReferee().getRefereeUUID() : new UUID(0, 0);
            gameAssignmentNode.put("refereeUUID", refereeUUID.toString());
        }
        gameAssignmentNode.put("indexInGame", gameAssignment.getIndexInGame());
        gameAssignmentNode.put("assignmentUUID", gameAssignment.getAssignmentUUID().toString());
        return gameAssignmentNode;
    }
    private JsonOutputConverter() {
    }
}
