package me.superbiebel.referee_planner.problemchanges.game;

import lombok.Builder;
import lombok.Getter;
import me.superbiebel.referee_planner.domain.Game;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.UUID;

public class GameExperienceChange extends GameProblemChange {
    @Getter
    private final int newExperience;
    @Getter
    private final EXPERIENCE_TYPE experienceType;
    @Builder(toBuilder = true)
    public GameExperienceChange(UUID gameUUID, int newExperience, EXPERIENCE_TYPE experienceType) {
        super(gameUUID);
        this.newExperience = newExperience;
        this.experienceType = experienceType;
    }
    
    @Override
    public Game doActualChange(Game game, RefereeTimeTable workingSolution, ProblemChangeDirector problemChangeDirector) {
        Game adaptedGame = null;
        switch (experienceType) {
            case HARD_MINIMUM:
                adaptedGame = game.withHardMinimumExperience(newExperience);
                break;
            case SOFT_MINIMUM:
                adaptedGame = game.withSoftMinimumExperience(newExperience);
                break;
            case SOFT_MAXIMUM:
                adaptedGame = game.withSoftMaximumExperience(newExperience);
                break;
        }
        return adaptedGame;
    }
    public enum EXPERIENCE_TYPE {
        HARD_MINIMUM, SOFT_MINIMUM, SOFT_MAXIMUM
    }
}
