package me.superbiebel.referee_planner.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import me.superbiebel.referee_planner.exceptions.LookupObjectNotFound;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@With
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
    @Builder(toBuilder = true)
    public Game(UUID gameUUID, List<GameAssignment> assignments, Location gameLocation, TimePeriod gamePeriod, int amountOfRefereesNeeded, int hardMinimumExperience, int softMinimumExperience, int softMaximumExperience, int priority) {
        this.gameUUID = gameUUID;
        this.assignments = assignments;
        this.gameLocation = gameLocation;
        this.gamePeriod = gamePeriod;
        this.gameRefereePeriod = TimePeriod.builder()
                                         .start(gamePeriod.getStart().minusMinutes(20))
                                         .end(gamePeriod.getEnd())
                                         .build();//the timePeriod that the referee has to be there
        this.amountOfRefereesNeeded = amountOfRefereesNeeded;
        this.hardMinimumExperience = hardMinimumExperience;
        this.softMinimumExperience = softMinimumExperience;
        this.softMaximumExperience = softMaximumExperience;
        this.priority = priority;
    }
    public Game(UUID gameUUID) { //ONLY FOR LOOKUP PURPOSES
        this.gameUUID = gameUUID;
    }

    public static Game lookupGameByUUID(UUID gameUUID, ProblemChangeDirector problemChangeDirector) {
        return problemChangeDirector.lookUpWorkingObject(new Game(gameUUID)).orElseThrow(LookupObjectNotFound::new);
    }
    
    public static List<GameAssignment> generateGameAssignments(Game game) {
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
    
    public void removeRefereesFromGameAssignments(ProblemChangeDirector problemChangeDirector) {
        assignments.forEach(gameAssignment -> removeRefereeFromGameAssignment(gameAssignment, problemChangeDirector));
    }
    public void removeRefereeFromGameAssignment(GameAssignment gameAssignment, ProblemChangeDirector problemChangeDirector) {
        if (gameAssignment.getReferee()!=null) {
            problemChangeDirector.changeProblemProperty(gameAssignment.getReferee(), referee -> referee.removeRefereeFromGameAssignment(gameAssignment, problemChangeDirector));
            problemChangeDirector.changeVariable(gameAssignment, "referee", gameAssignment1 -> gameAssignment1.setReferee(null));
        }
    }
    public void addAssignment(GameAssignment assignment) {
        assignment.setGame(this);
        assignments.add(assignment);
    }
    public void addAssignment(GameAssignment gameAssignment, ProblemChangeDirector problemChangeDirector) {
        problemChangeDirector.changeProblemProperty(this, game -> game.getAssignments().add(gameAssignment));
    }
    public void removeGameAssignment(GameAssignment gameAssignment, ProblemChangeDirector problemChangeDirector) {
        removeRefereeFromGameAssignment(gameAssignment, problemChangeDirector);
        problemChangeDirector.changeProblemProperty(this, game -> game.getAssignments().remove(gameAssignment));
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
