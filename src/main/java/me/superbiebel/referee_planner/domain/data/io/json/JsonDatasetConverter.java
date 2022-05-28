package me.superbiebel.referee_planner.domain.data.io.json;

import com.fasterxml.jackson.databind.JsonNode;
import me.superbiebel.referee_planner.domain.*;
import me.superbiebel.referee_planner.domain.data.RandomDataGenerator;
import me.superbiebel.referee_planner.domain.data.TimeTableGenerator;

import java.time.LocalDateTime;
import java.util.*;

public class JsonDatasetConverter {
    
    public static RefereeTimeTable generateTimeTableFromJson(JsonNode timeTableNode, boolean randomGameAssignments) {
        List<Game> gameList = new ArrayList<>();
        List<GameAssignment> gameAssignmentList = new ArrayList<>();
        timeTableNode.get("games").elements().forEachRemaining(gameNode -> {
            Game game = generateGame(gameNode);
            gameList.add(game);
            if (randomGameAssignments) {
                gameAssignmentList.addAll(generateRandomGameAssignmentsFromGame(game));
            }
        });
        List<Referee> refereeList = new ArrayList<>();
        timeTableNode.get("referees").elements().forEachRemaining(refereeNode -> refereeList.add(generateReferee(refereeNode)));
        
        Map<UUID, Game> gameMap = new HashMap<>();
        gameList.forEach(game -> gameMap.put(game.getGameUUID(), game));
    
        Map<UUID, Referee> refereeMap = new HashMap<>();
        refereeList.forEach(referee -> refereeMap.put(referee.getRefereeUUID(), referee));
        
        if (!randomGameAssignments) {
            timeTableNode.get("assignments").elements().forEachRemaining(jsonNode -> gameAssignmentList.add(generateGameAssignment(jsonNode, gameMap, refereeMap)));
        }
        return RefereeTimeTable.builder()
                       .referees(refereeList)
                       .gameAssignments(gameAssignmentList)
                       .games(gameList)
                       .refereeTimeTableUUID(UUID.fromString(timeTableNode.get("timeTableUUID").asText()))
                       .build();
    }
    
    public static Game generateGame(JsonNode gameNode) {
        return Game.builder()
                       .gameUUID(UUID.fromString(gameNode.get("gameUUID").asText()))
                       .gameLocation(generateLocation(gameNode.get("gameLocation")))
                       .gamePeriod(generateTimePeriod(gameNode.get("gamePeriod")))
                       .amountOfRefereesNeeded(gameNode.get("amountOfRefereesNeeded").asInt())
                       .hardMinimumExperience(gameNode.get("hardMinimumExperience").asInt())
                       .softMinimumExperience(gameNode.get("softMinimumExperience").asInt())
                       .softMaximumExperience(gameNode.get("softMaximumExperience").asInt())
                       .priority(gameNode.get("priority").asInt())
                       .build();
    }
    
    public static Referee generateReferee(JsonNode refereeNode) {
        if (refereeNode.get("isNonExist").booleanValue()) {
            return TimeTableGenerator.VIRTUAL_REFEREE;
        }
        List<Availability> availabilities = new ArrayList<>();
        refereeNode.get("availability").elements().forEachRemaining(periodNode -> availabilities.add(generateAvailability(periodNode)));
        return Referee.builder()
                       .refereeUUID(UUID.fromString(refereeNode.get("refereeUUID").asText()))
                       .experience(refereeNode.get("experience").asInt())
                       .availabilityList(availabilities)
                       .build();
    }
    
    public static Availability generateAvailability(JsonNode availabilityNode) {
        Availability.AvailabilityBuilder builder = Availability.builder()
                                                           .availabilityUUID(UUID.fromString(availabilityNode.get("availabilityUUID").asText()))
                                                           .startLocation(generateLocation(availabilityNode.get("startLocation")))
                                                           .timePeriod(generateTimePeriod(availabilityNode.get("timeperiod")))
                                                           .endLocationEnabled(availabilityNode.get("endLocationEnabled").asBoolean())
                                                           .maxRangeEnabled(availabilityNode.get("maxRangeEnabled").asBoolean());
        if (availabilityNode.get("endLocationEnabled").asBoolean()) {
            builder.endLocation(generateLocation(availabilityNode.get("endLocation")));
        }
        if (availabilityNode.get("maxRangeEnabled").asBoolean()) {
            builder.maxRange(availabilityNode.get("maxRange").asLong());
        } else {
            builder.maxRange(-1);
        }
        return builder.build();
    }
    
    public static Location generateLocation(JsonNode locationNode) {
        return Location.builder()
                       .latitude(locationNode.get("latitude").asDouble())
                       .longitude(locationNode.get("longitude").asDouble())
                       .build();
    }
    
    public static TimePeriod generateTimePeriod(JsonNode timePeriodNode) {
        return TimePeriod.builder()
                       .start(generateDateTime(timePeriodNode.get("start")))
                       .end(generateDateTime(timePeriodNode.get("end")))
                       .build();
    }
    
    public static LocalDateTime generateDateTime(JsonNode dateTimeNode) {
        return LocalDateTime.of(dateTimeNode.get("year").asInt(),
                dateTimeNode.get("month").asInt(),
                dateTimeNode.get("day").asInt(),
                dateTimeNode.get("hour").asInt(),
                dateTimeNode.get("minute").asInt());
    }
    
    public static GameAssignment generateGameAssignment(JsonNode gameAssignmentNode, Map<UUID, Game> gameMap, Map<UUID, Referee> refereeMap) {
        Game game = gameMap.get(UUID.fromString(gameAssignmentNode.get("gameUUID").asText()));
        GameAssignment assignment = GameAssignment.builder()
                                            .assignmentUUID(UUID.fromString(gameAssignmentNode.get("assignmentUUID").asText()))
                                            .indexInGame(gameAssignmentNode.get("indexInGame").asInt())
                                            .game(game)
                                            .referee(refereeMap.get(UUID.fromString(gameAssignmentNode.get("refereeUUID").asText())))
                                            .build();
        game.getAssignments().add(assignment);
        return assignment;
    }
    
    public static List<GameAssignment> generateRandomGameAssignmentsFromGame(Game game) {
        return RandomDataGenerator.generateGameAssignments(game);
    }
    
    private JsonDatasetConverter() {
    }
}
