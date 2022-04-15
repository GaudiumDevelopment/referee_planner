package me.superbiebel.referee_planner.domain;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
public class Game {
    @Getter
    private Location gameLocation;
    @Getter
    private TimePeriod gamePeriod;
    @Getter
    private int amountOfRefereesNeeded;
    
    //experience
    @Getter
    private int hardMinimumExperience;
    @Getter
    private int softMinimumExperience;
    @Getter
    private int softMaximumExperience;
    
    public Game() {
    }
    
    public Game(Location gameLocation, TimePeriod gamePeriod, int amountOfRefereesNeeded, int hardMinimumExperience, int softMinimumExperience, int softMaximumExperience) {
        this.gameLocation = gameLocation;
        this.gamePeriod = gamePeriod;
        this.amountOfRefereesNeeded = amountOfRefereesNeeded;
        this.hardMinimumExperience = hardMinimumExperience;
        this.softMinimumExperience = softMinimumExperience;
        this.softMaximumExperience = softMaximumExperience;
    }
}
