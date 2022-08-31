package me.superbiebel.referee_planner.domain.data;

import me.superbiebel.referee_planner.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RandomDataGenerator {
    
    public static final Random random = new Random();
    
    public static final LocalDate BEGIN_DATE = LocalDate.of(2022, 1, 1);
    public static final LocalDate END_DATE = LocalDate.of(2022, 1, 2);
    public static final LocalTime BEGIN_REFEREE_TIME = LocalTime.of(6, 0);
    public static final LocalTime BEGIN_GAME_TIME = LocalTime.of(8, 0);
    public static final LocalTime END_REFEREE_TIME = LocalTime.of(23, 0);
    public static final LocalTime END_GAME_TIME = LocalTime.of(20, 0); //when the last match can still begin
    
    public static final LocalDateTime BEGIN_REFEREE_DATE_TIME = LocalDateTime.of(BEGIN_DATE, BEGIN_REFEREE_TIME);
    public static final LocalDateTime END_REFEREE_DATE_TIME = LocalDateTime.of(END_DATE, END_REFEREE_TIME);
    
    public static final LocalDateTime BEGIN_GAME_DATE_TIME = LocalDateTime.of(BEGIN_DATE, BEGIN_GAME_TIME);
    public static final LocalDateTime END_GAME_DATE_TIME = LocalDateTime.of(END_DATE, END_GAME_TIME); //until the last game can go
    
    public static final ZoneOffset ZONE = ZoneOffset.UTC;
    
    private RandomDataGenerator() {
    }
    
    public static Game generateGame() {
        int hardMinimum = generateIntInRange(0, 40, true);
        int softMinimum = generateIntInRange(hardMinimum, 95, true);
        int softMaximum = generateIntInRange(softMinimum, 100, true);
        
        Game.GameBuilder builder = Game.builder()
                                           .gameUUID(UUID.randomUUID())
                                           .gameLocation(giveLocationWithinBelgium())
                                           .amountOfRefereesNeeded(generateIntInRange(2, 3, true))
                                           .gamePeriod(generateTimePeriodForGame(
                                                   LocalDate.of(
                                                           generateIntInRange(BEGIN_DATE.getYear(), END_DATE.getYear(), true),
                                                           generateIntInRange(BEGIN_DATE.getMonthValue(), END_DATE.getMonthValue(), true),
                                                           generateIntInRange(BEGIN_DATE.getDayOfMonth(), END_DATE.getDayOfMonth(), true))))
                                           .hardMinimumExperience(hardMinimum)
                                           .softMinimumExperience(softMinimum)
                                           .softMaximumExperience(softMaximum)
                                           .priority(generateIntInRange(1, 50, true));
        return builder.build();
    }
    
    public static TimePeriod generateTimePeriodForGame(LocalDate date) {
        LocalDateTime periodBegin = generateRandomLocalDateTime(LocalDateTime.of(date, BEGIN_GAME_TIME), LocalDateTime.of(date, END_GAME_TIME).minusHours(2));
        return TimePeriod.builder().start(periodBegin).end(periodBegin.plusHours(2)).build();
    }
    
    public static List<Availability> generateAvailabilityListForReferee(int dayCount) {
        List<Availability> availabilityList = new ArrayList<>();
        for (int i = 0; i < dayCount + 1; i++) {
            TimePeriod timePeriod = generateAvailabilityTimePeriod(BEGIN_DATE.plusDays(i));
            Location startLocation = giveLocationWithinBelgium();
            Location endLocation;
            if (generateIntInRange(0, 5, true) == 1) {
                endLocation = giveLocationWithinBelgium();
            } else {
                endLocation = startLocation;
            }
            boolean endLocationEnabled = generateIntInRange(0, 5, true) == 1;
            long maxRange = -1;
            boolean maxRangeEnabled = false;
            //noinspection AssignmentUsedAsCondition
            if (maxRangeEnabled = (generateIntInRange(1, 20, true) == 1)) {
                maxRange = generateLongInRange(1, 190_000/*in meter*/, true);
            }
            availabilityList.add(Availability.builder()
                                         .availabilityUUID(UUID.randomUUID())
                                         .startLocation(startLocation)
                                         .endLocation(endLocation)
                                         .endLocationEnabled(endLocationEnabled)
                                         .timePeriod(timePeriod)
                                         .maxRangeEnabled(maxRangeEnabled)
                                         .maxRange(maxRange)
                                         .build());
        }
        return availabilityList;
    }
    
    public static TimePeriod generateAvailabilityTimePeriod(LocalDate date) {
        LocalDateTime periodBegin = generateRandomLocalDateTime(
                LocalDateTime.of(date, BEGIN_REFEREE_TIME),
                LocalDateTime.of(date, END_REFEREE_TIME.minusHours(3)));
        LocalDateTime periodEnd = generateRandomLocalDateTime(periodBegin, LocalDateTime.of(date, END_REFEREE_TIME));
        return TimePeriod.builder().start(periodBegin).end(periodEnd).build();
    }
    
    public static LocalDateTime generateRandomLocalDateTime(LocalDateTime begin, LocalDateTime end) {
        LocalDateTime generateRandomLocalDateTime = LocalDateTime.ofEpochSecond(generateLongInRange(begin.toEpochSecond(ZONE), end.toEpochSecond(ZONE), true), 0, ZONE);
        return LocalDateTime.of(generateRandomLocalDateTime.toLocalDate(), LocalTime.of(generateRandomLocalDateTime.getHour(), generateRandomLocalDateTime.getMinute()));
    }
    
    public static Referee generateReferee() {
        return Referee.builder()
                       .refereeUUID(UUID.randomUUID())
                       .experience(generateIntInRange(0, 100, true))
                       .availabilityList(generateAvailabilityListForReferee(2))
                       .build();
    }
    
    public static Location giveLocationWithinBelgium() {
        return generateRandomLocation(51.413828127695915, 2.886469282111513, 50.815988855750156, 5.660311233686485);
    }
    
    public static Location generateRandomLocation(double latitude1, double longitude1, double latitude2, double longitude2) {
        double generatedLatitude = latitude1 < latitude2 ? generateRandomDoubleInRange(latitude1, latitude2) : generateRandomDoubleInRange(latitude2, latitude1);
        double generatedLongitude = longitude1 < longitude2 ? generateRandomDoubleInRange(longitude1, longitude2) : generateRandomDoubleInRange(longitude2, longitude1);
        return Location.builder().latitude(generatedLatitude).longitude(generatedLongitude).build();
    }
    
    public static double generateRandomDoubleInRange(double rangeMin, double rangeMax) {
        return rangeMin + (rangeMax - rangeMin) * random.nextDouble();
    }
    
    public static int generateIntInRange(int min, int max, boolean shouldIncreaseMax) {
        if (shouldIncreaseMax) max++;
        return ThreadLocalRandom.current().nextInt(min, max);
    }
    
    public static long generateLongInRange(long min, long max, boolean shouldIncreaseMax) {
        if (shouldIncreaseMax) max++;
        return ThreadLocalRandom.current().nextLong(min, max);
    }
}
