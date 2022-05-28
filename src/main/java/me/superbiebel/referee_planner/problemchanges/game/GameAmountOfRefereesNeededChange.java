package me.superbiebel.referee_planner.problemchanges.game;

import lombok.Builder;
import lombok.Getter;
import me.superbiebel.referee_planner.domain.Game;
import me.superbiebel.referee_planner.domain.GameAssignment;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.List;
import java.util.UUID;

public class GameAmountOfRefereesNeededChange extends GameProblemChange {
    @Getter
    private final int newAmount;
    @Builder(toBuilder = true)
    public GameAmountOfRefereesNeededChange(UUID gameUUID, int newAmount) {
        super(gameUUID);
        this.newAmount = newAmount;
    }
    
    @Override
    public Game doActualChange(Game game, RefereeTimeTable workingSolution, ProblemChangeDirector problemChangeDirector) {
        int oldAmount = game.getAmountOfRefereesNeeded();
        Game adaptedGame = game.withAmountOfRefereesNeeded(newAmount);
    
        int newAmountMaxIndex = newAmount-1;
    
        if (oldAmount > newAmount) {
            List<GameAssignment> assignments = game.getAssignments();
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, assignmentsSize = assignments.size(); i < assignmentsSize; i++) { // it has GOT to be this kind of loop otherwise you get a ConcurrentModificationException
                GameAssignment gameAssignment = problemChangeDirector.lookUpWorkingObject(GameAssignment.builder().assignmentUUID(assignments.get(i).getAssignmentUUID()).build()).orElseThrow();
                if (gameAssignment.getIndexInGame() > newAmountMaxIndex) {
                    problemChangeDirector.changeProblemProperty(gameAssignment, gameAssignment1 -> gameAssignment1.getReferee().removeRefereeFromGameAssignment(gameAssignment, problemChangeDirector));
                    problemChangeDirector.removeEntity(gameAssignment, gameAssignment1 -> workingSolution.getGameAssignments().remove(gameAssignment1));
                    adaptedGame.getAssignments().remove(gameAssignment);
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
                adaptedGame.addAssignment(newGameAssignment);
            }
        }
        return adaptedGame;
    }
}
