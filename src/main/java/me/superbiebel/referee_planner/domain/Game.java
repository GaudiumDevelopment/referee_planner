package me.superbiebel.referee_planner.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.optaplanner.core.api.domain.lookup.PlanningId;

import java.util.List;
import java.util.UUID;

@SuppressFBWarnings("EI_EXPOSE_REP2")
@Builder(toBuilder = true)
public class Game {
    @PlanningId
    @Getter
    private UUID gameUUID;
    
    @SuppressFBWarnings("EI_EXPOSE_REP")
    @JsonIgnore
    @Setter
    @Getter
    private List<GameAssignment> assignments;
    
    @Getter
    private Location gameLocation;
    @Getter
    private TimePeriod gameRefereePeriod; //the period that the referee has to be there
    @Getter
    private int amountOfRefereesNeeded;
    
    //experience
    @Getter
    private int hardMinimumExperience;
    @Getter
    private int softMinimumExperience;
    @Getter
    private int softMaximumExperience;
    @Getter
    private int priority;
    
    public Game() {
    }
    
    public Game(UUID gameUUID, List<GameAssignment> assignments, Location gameLocation, TimePeriod gameRefereePeriod, int amountOfRefereesNeeded, int hardMinimumExperience, int softMinimumExperience, int softMaximumExperience) {
        this.gameUUID = gameUUID;
        this.assignments = assignments;
        this.gameLocation = gameLocation;
        this.gameRefereePeriod = gameRefereePeriod;
        this.amountOfRefereesNeeded = amountOfRefereesNeeded;
        this.hardMinimumExperience = hardMinimumExperience;
        this.softMinimumExperience = softMinimumExperience;
        this.softMaximumExperience = softMaximumExperience;
    }
    
    public Game(UUID gameUUID, List<GameAssignment> assignments, Location gameLocation, TimePeriod gameRefereePeriod, int amountOfRefereesNeeded, int hardMinimumExperience, int softMinimumExperience, int softMaximumExperience, int priority) {
        this.gameUUID = gameUUID;
        this.assignments = assignments;
        this.gameLocation = gameLocation;
        this.gameRefereePeriod = gameRefereePeriod;
        this.amountOfRefereesNeeded = amountOfRefereesNeeded;
        this.hardMinimumExperience = hardMinimumExperience;
        this.softMinimumExperience = softMinimumExperience;
        this.softMaximumExperience = softMaximumExperience;
        this.priority = priority;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Game{");
        sb.append("gameUUID=").append(gameUUID);
        sb.append(", gameLocation=").append(gameLocation);
        sb.append(", gameRefereePeriod=").append(gameRefereePeriod);
        sb.append(", amountOfRefereesNeeded=").append(amountOfRefereesNeeded);
        sb.append(", hardMinimumExperience=").append(hardMinimumExperience);
        sb.append(", softMinimumExperience=").append(softMinimumExperience);
        sb.append(", softMaximumExperience=").append(softMaximumExperience);
        sb.append('}');
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Game game = (Game) o;
        
        if (getAmountOfRefereesNeeded() != game.getAmountOfRefereesNeeded()) return false;
        if (getHardMinimumExperience() != game.getHardMinimumExperience()) return false;
        if (getSoftMinimumExperience() != game.getSoftMinimumExperience()) return false;
        if (getSoftMaximumExperience() != game.getSoftMaximumExperience()) return false;
        if (!getGameUUID().equals(game.getGameUUID())) return false;
        if (!getGameLocation().equals(game.getGameLocation())) return false;
        return getGameRefereePeriod().equals(game.getGameRefereePeriod());
    }
    
    @Override
    public int hashCode() {
        int result = getGameUUID().hashCode();
        result = 31 * result + getGameLocation().hashCode();
        result = 31 * result + getGameRefereePeriod().hashCode();
        result = 31 * result + getAmountOfRefereesNeeded();
        result = 31 * result + getHardMinimumExperience();
        result = 31 * result + getSoftMinimumExperience();
        result = 31 * result + getSoftMaximumExperience();
        return result;
    }
}
