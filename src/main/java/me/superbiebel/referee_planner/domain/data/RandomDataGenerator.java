package me.superbiebel.referee_planner.domain.data;

import me.superbiebel.referee_planner.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RandomDataGenerator {
    
    public static final Random random = new Random();
    
    public static final LocalDateTime ZERO_DATE_TIME = LocalDateTime.of(2022, 1, 1, 1, 1);
    
    private RandomDataGenerator() {
    }
    
    public static Game generateGame() {
        return Game.builder()
                       .gameUUID(UUID.randomUUID())
                       .gameLocation(giveLocationWithinBelgium())
                       .amountOfRefereesNeeded(generateIntInRange(2, 4))
                       .gameRefereePeriod(generateTimePeriodForGame())
                       .hardMinimumExperience(generateIntInRange(0, 50))
                       .softMinimumExperience(generateIntInRange(51, 75))
                       .softMaximumExperience(generateIntInRange(76, 101))
                       .priority(generateIntInRange(0, 51))
                       .build();
    }
    
    public static TimePeriod generateTimePeriodForGame() {
        LocalDateTime time = ZERO_DATE_TIME.plusDays(generateIntInRange(1, 10)).plusHours(generateIntInRange(2, 24)).plusMinutes(generateIntInRange(1, 60));
        return TimePeriod.builder().start(time).end(time.plusHours(2)).build();
    }
    
    public static TimePeriod generateTimePeriodForReferee() {
        LocalDateTime time = ZERO_DATE_TIME.plusDays(generateIntInRange(0, 10)).plusHours(generateIntInRange(0, 24)).plusMinutes(generateIntInRange(1, 60));
        return TimePeriod.builder().start(time).end(time.plusHours(generateIntInRange(3, 6))).build();
    }
    
    public static List<GameAssignment> generateGameAssignment(Game game) {
        List<GameAssignment> assignmentList = new ArrayList<>();
        for (int i = 0; i < game.getAmountOfRefereesNeeded(); i++) {
            assignmentList.add(GameAssignment.builder()
                                       .game(game)
                                       .indexInGame(i)
                                       .assignmentUUID(UUID.randomUUID())
                                       .build());
        }
        game.setAssignments(assignmentList);
        return assignmentList;
    }
    public static Referee generateReferee() {
        List<TimePeriod> availabilityList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            availabilityList.add(generateTimePeriodForReferee());
        }
        availabilityList = TimePeriod.compactAndSortTimePeriods(availabilityList);
        return Referee.builder()
                       .refereeUUID(UUID.randomUUID())
                       .experience(generateIntInRange(0, 100))
                       .homeLocation(giveLocationWithinBelgium())
                       .availabilityList(availabilityList)
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
