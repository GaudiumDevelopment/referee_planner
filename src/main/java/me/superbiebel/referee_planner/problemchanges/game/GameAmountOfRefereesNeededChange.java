package me.superbiebel.referee_planner.problemchanges.game;

import lombok.Builder;
import lombok.Getter;
import me.superbiebel.referee_planner.domain.Game;
import me.superbiebel.referee_planner.domain.GameAssignment;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.UUID;

@Builder(toBuilder = true)
public class GameAmountOfRefereesNeededChange implements ProblemChange<RefereeTimeTable> {
    @Getter
    private UUID gameUUID;
    @Getter
    private int newAmount;
    @Override
    public void doChange(RefereeTimeTable workingSolution, ProblemChangeDirector problemChangeDirector) {
        Game game = Game.lookupGameByUUID(gameUUID, problemChangeDirector);
        int oldAmount = game.getAmountOfRefereesNeeded();
        problemChangeDirector.changeProblemProperty(game, game1 -> game1.setAmountOfRefereesNeeded(newAmount));
        if (oldAmount > newAmount) {
            game.getAssignments().forEach(gameAssignment -> {
                if (gameAssignment.getIndexInGame() < newAmount) {
                    problemChangeDirector.removeEntity(gameAssignment, gameAssignment1 -> workingSolution.getGameAssignments().remove(gameAssignment1));
                    problemChangeDirector.changeProblemProperty(game, game1 -> game1.getAssignments().remove(gameAssignment));
                }
            });
        } else if (oldAmount < newAmount){
            for (int i = oldAmount; i < newAmount; i++) {
                GameAssignment newGameAssignment = GameAssignment.builder()
                                                           .assignmentUUID(UUID.randomUUID())
                                                           .referee(null)
                                                           .indexInGame(i-1)
                                                           .build();
                problemChangeDirector.addEntity(newGameAssignment, workingSolution1->workingSolution.getGameAssignments().add(newGameAssignment));
            }
        }
        game.removeRefereesFromGameAssignments(problemChangeDirector);
    }
}
