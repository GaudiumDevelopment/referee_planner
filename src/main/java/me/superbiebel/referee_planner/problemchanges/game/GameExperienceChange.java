package me.superbiebel.referee_planner.problemchanges.game;

import lombok.Builder;
import lombok.Getter;
import me.superbiebel.referee_planner.domain.Game;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.UUID;

@Builder(toBuilder = true)
public class GameExperienceChange implements ProblemChange<RefereeTimeTable> {
    @Getter
    private UUID gameUUID;
    @Getter
    private int newExperience;
    @Getter
    private EXPERIENCE_TYPE experienceType;
    @Override
    public void doChange(RefereeTimeTable workingSolution, ProblemChangeDirector problemChangeDirector) {
        Game game = Game.lookupGameByUUID(gameUUID, problemChangeDirector);
        problemChangeDirector.changeProblemProperty(game, game1 -> {
            switch (experienceType) {
                case HARD_MINIMUM:
                    game1.setHardMinimumExperience(newExperience);
                    break;
                case SOFT_MINIMUM:
                    game1.setSoftMinimumExperience(newExperience);
                    break;
                case SOFT_MAXIMUM:
                    game1.setSoftMaximumExperience(newExperience);
                    break;
            }
        });
        game.removeRefereesFromGameAssignments(problemChangeDirector);
    }
    enum EXPERIENCE_TYPE {
        HARD_MINIMUM, SOFT_MINIMUM, SOFT_MAXIMUM
    }
}
