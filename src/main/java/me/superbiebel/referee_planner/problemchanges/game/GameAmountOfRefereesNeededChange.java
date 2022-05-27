package me.superbiebel.referee_planner.problemchanges.game;

import lombok.Builder;
import lombok.Getter;
import me.superbiebel.referee_planner.domain.Game;
import me.superbiebel.referee_planner.domain.GameAssignment;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.List;
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
        
        int newAmountMaxIndex = newAmount-1;
        
        if (oldAmount > newAmount) {
            List<GameAssignment> assignments = game.getAssignments();
            for (int i = 0, assignmentsSize = assignments.size(); i < assignmentsSize; i++) { // it has GOT to be this kind of loop otherwise you get a ConcurrentModificationException
                GameAssignment gameAssignment = assignments.get(i);
                if (gameAssignment.getIndexInGame() > newAmountMaxIndex) {
                    problemChangeDirector.changeProblemProperty(gameAssignment, gameAssignment1 -> gameAssignment1.getReferee().removeRefereeFromGameAssignment(gameAssignment, problemChangeDirector));
                    problemChangeDirector.removeEntity(gameAssignment, gameAssignment1 -> workingSolution.getGameAssignments().remove(gameAssignment1));
                    problemChangeDirector.changeProblemProperty(game, game1 -> game1.removeGameAssignment(gameAssignment, problemChangeDirector));
                }
            }
        } else if (oldAmount < newAmount){
            for (int i = oldAmount; i <= newAmountMaxIndex; i++) {
                GameAssignment newGameAssignment = GameAssignment.builder()
                                                           .assignmentUUID(UUID.randomUUID())
                                                           .referee(null)
                                                           .game(game)
                                                           .indexInGame(i)
                                                           .build();
                problemChangeDirector.addEntity(newGameAssignment, workingSolution1->workingSolution.getGameAssignments().add(newGameAssignment));
                game.addAssignment(newGameAssignment, problemChangeDirector);
            }
        }
    }
}
