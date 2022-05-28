package me.superbiebel.referee_planner.problemchanges.game;

import me.superbiebel.referee_planner.domain.Game;
import me.superbiebel.referee_planner.domain.GameAssignment;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public abstract class GameProblemChange implements ProblemChange<RefereeTimeTable> {
    protected UUID gameUUID;
    protected GameProblemChange(UUID gameUUID) {
        this.gameUUID = gameUUID;
    }
    
    @Override
    public void doChange(RefereeTimeTable workingSolution, ProblemChangeDirector problemChangeDirector) {
        Game game = Game.lookupGameByUUID(gameUUID, problemChangeDirector);
        game.removeRefereesFromGameAssignments(problemChangeDirector);
    
        workingSolution.setGames(new ArrayList<>(workingSolution.getGames()));
    
        problemChangeDirector.removeProblemFact(game, game1-> workingSolution.getGames().remove(game1));
        
        Game adaptedGame = doActualChange(game, workingSolution, problemChangeDirector);
        
        for (GameAssignment gameAssignment1 : game.getAssignments()) {
            Optional<GameAssignment> optionalGameAssignment = problemChangeDirector.lookUpWorkingObject(GameAssignment.builder().assignmentUUID(gameAssignment1.getAssignmentUUID()).build());
            if (optionalGameAssignment.isPresent()) {
                gameAssignment1.setGame(adaptedGame);
            }
        }
    
        problemChangeDirector.addProblemFact(adaptedGame, adaptedGame1-> workingSolution.getGames().add(adaptedGame1));
    }
    public abstract Game doActualChange(Game game, RefereeTimeTable workingSolution, ProblemChangeDirector problemChangeDirector);
}
