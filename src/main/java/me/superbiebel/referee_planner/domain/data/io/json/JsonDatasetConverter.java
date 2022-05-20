package me.superbiebel.referee_planner.domain.data.io.json;

import com.fasterxml.jackson.databind.JsonNode;
import me.superbiebel.referee_planner.domain.*;
import me.superbiebel.referee_planner.domain.data.RandomDataGenerator;
import me.superbiebel.referee_planner.domain.data.TimeTableGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JsonDatasetConverter {
    
    public static RefereeTimeTable generateTimeTableFromJson(JsonNode timeTableNode) {
        List<Game> gameList = new ArrayList<>();
        List<GameAssignment> gameAssignmentList = new ArrayList<>();
        timeTableNode.get("games").elements().forEachRemaining(gameNode -> {
            Game game = generateGame(gameNode);
            gameList.add(game);
            gameAssignmentList.addAll(generateGameAssignmentsFromGame(game));
        });
        List<Referee> refereeList = new ArrayList<>();
        timeTableNode.get("referees").elements().forEachRemaining(refereeNode -> refereeList.add(generateReferee(refereeNode)));
        return RefereeTimeTable.builder()
                       .referees(refereeList)
                       .gameAssignments(gameAssignmentList)
                       .games(gameList)
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
        List<Availability> timePeriods = new ArrayList<>();
        refereeNode.get("availability").elements().forEachRemaining(periodNode -> timePeriods.add(generateAvailability(periodNode)));
        return Referee.builder()
                       .refereeUUID(UUID.fromString(refereeNode.get("refereeUUID").asText()))
                       .experience(refereeNode.get("experience").asInt())
                       .availabilityList(timePeriods)
                       .build();
    }
    
    public static Availability generateAvailability(JsonNode availabilityNode) {
        Availability.AvailabilityBuilder builder = Availability.builder()
                                                           .startLocation(generateLocation(availabilityNode.get("startLocation")))
                                                           .timePeriod(generateTimePeriod(availabilityNode.get("timeperiod")))
                                                           .endLocationEnabled(availabilityNode.get("endLocationEnabled").asBoolean());
        if (availabilityNode.get("endLocationEnabled").asBoolean()) {
            builder.endLocation(generateLocation(availabilityNode.get("endLocation")));
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
    
    public static GameAssignment generateGameAssignment(JsonNode gameAssignmentNode, Map<UUID, Game> gameMap) {
        return GameAssignment.builder()
                       .assignmentUUID(UUID.fromString(gameAssignmentNode.get("assignmentUUID").asText()))
                       .indexInGame(gameAssignmentNode.get("indexInGame").asInt())
                       .game(gameMap.get(UUID.fromString(gameAssignmentNode.get("gameUUID").asText())))
                       .build();
    }
    
    public static List<GameAssignment> generateGameAssignmentsFromGame(Game game) {
        return RandomDataGenerator.generateGameAssignments(game);
    }
    
    private JsonDatasetConverter() {
    }
}
