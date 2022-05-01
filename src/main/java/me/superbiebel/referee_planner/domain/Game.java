package me.superbiebel.referee_planner.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public class Game {
    
    @Getter
    private UUID gameUUID;
    
    @JsonIgnore
    @Setter
    @Getter
    private List<GameAssignment> assignments;
    
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
    
    public Game(UUID gameUUID, List<GameAssignment> assignments, Location gameLocation, TimePeriod gamePeriod, int amountOfRefereesNeeded, int hardMinimumExperience, int softMinimumExperience, int softMaximumExperience) {
        this.gameUUID = gameUUID;
        this.assignments = assignments;
        this.gameLocation = gameLocation;
        this.gamePeriod = gamePeriod;
        this.amountOfRefereesNeeded = amountOfRefereesNeeded;
        this.hardMinimumExperience = hardMinimumExperience;
        this.softMinimumExperience = softMinimumExperience;
        this.softMaximumExperience = softMaximumExperience;
    }
}
