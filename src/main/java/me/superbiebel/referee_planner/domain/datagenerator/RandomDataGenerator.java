package me.superbiebel.referee_planner.domain.datagenerator;

import me.superbiebel.referee_planner.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RandomDataGenerator {
    
    public static final Random random = new Random();
    
    private RandomDataGenerator() {
    }
    
    public static Game generateGame() {
        
        return Game.builder()
                       .gameUUID(UUID.randomUUID())
                       .gameLocation(giveLocationWithinBelgium())
                       .amountOfRefereesNeeded(generateIntInRange(1, 4))
                       .gamePeriod(generateTimePeriod())
                       .hardMinimumExperience(generateIntInRange(0,50))
                       .softMinimumExperience(generateIntInRange(51,75))
                       .softMaximumExperience(generateIntInRange(76,100))
                       .build();
    }
    
    public static TimePeriod generateTimePeriod() {
        LocalDateTime time = LocalDateTime.now().plusDays(generateIntInRange(0,10));
        return TimePeriod.builder().start(time).end(time.plusHours(2)).build();
    }
    public static List<GameAssignment> generateGameAssignment(Game game) {
        ArrayList<GameAssignment> assignmentList = new ArrayList<>();
        for (int i = 0; i < game.getAmountOfRefereesNeeded(); i++) {
            assignmentList.add(GameAssignment.builder()
                                       .game(game)
                                       .indexInGame(i)
                                       .build());
        }
        game.setAssignments(assignmentList);
        return assignmentList;
    }
    public static Referee generateReferee() {
        return Referee.builder()
                       .uuid(UUID.randomUUID())
                       .experience(generateIntInRange(0,100))
                       .homeLocation(giveLocationWithinBelgium())
                       .build();
    }
    private static Location giveLocationWithinBelgium() {
        return generateRandomLocation(51.338577536734896, 3.2130495807239408,49.596303708297754, 5.8097190890112085);
    }
    
    public static Location generateRandomLocation(double latitude1, double longitude1, double latitude2, double longitude2) {
        return Location.builder().latitude(generateRandomDoubleInRange(latitude1,latitude2)).longitude(generateRandomDoubleInRange(longitude1,longitude2)).build();
    }
    public static double generateRandomDoubleInRange(double rangeMin, double rangeMax) {
        return rangeMin + (rangeMax - rangeMin) * random.nextDouble();
    }
    public static int generateIntInRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }
}
