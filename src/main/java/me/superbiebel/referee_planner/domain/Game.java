package me.superbiebel.referee_planner.domain;

import lombok.Getter;

import java.time.LocalDateTime;

public class Game {
    @Getter
    private Location gameLocation;
    @Getter
    private LocalDateTime arrivalTime;
    @Getter
    private LocalDateTime departureTime;
    @Getter
    private int amountOfRefereesNeeded;
    
    //experience
    @Getter
    private int hardMinimumExperience;
    @Getter
    private int softMinimumExperience;
    @Getter
    private int softMaximumExperience;
}
