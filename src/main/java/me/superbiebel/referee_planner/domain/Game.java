package me.superbiebel.referee_planner.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.superbiebel.referee_planner.exceptions.LookupObjectNotFound;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
@AllArgsConstructor
public class Game {
    @PlanningId
    @Getter
    private UUID gameUUID;
    
    @JsonIgnore
    @Setter
    @Getter
    private List<GameAssignment> assignments;
    
    @Getter
    @Setter
    private Location gameLocation;
    @Getter
    @Setter
    private TimePeriod gamePeriod;
    @Getter
    @Setter
    private TimePeriod gameRefereePeriod;
    @Getter
    @Setter
    private int amountOfRefereesNeeded;
    
    //experience
    @Getter
    @Setter
    private int hardMinimumExperience;
    @Getter
    @Setter
    private int softMinimumExperience;
    @Getter
    @Setter
    private int softMaximumExperience;
    @Getter
    @Setter
    private int priority;
    
    public Game() {
        //for optaplanner
    }

    public static Game lookupGameByUUID(UUID gameUUID, ProblemChangeDirector problemChangeDirector) {
        return problemChangeDirector.lookUpWorkingObject(Game.builder().gameUUID(gameUUID).build()).orElseThrow(LookupObjectNotFound::new);
    }

    public void removeRefereesFromGameAssignments(ProblemChangeDirector problemChangeDirector) {
        assignments.forEach(gameAssignment -> problemChangeDirector.changeVariable(gameAssignment, "referee", gameAssignment1 -> gameAssignment1.setReferee(null)));
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
