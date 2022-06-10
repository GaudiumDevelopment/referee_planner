package me.superbiebel.tests.problemchange;

import io.quarkus.test.junit.QuarkusTest;
import me.superbiebel.referee_planner.domain.Referee;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import me.superbiebel.referee_planner.domain.data.TimeTableGenerator;
import me.superbiebel.referee_planner.problemchanges.gameassignment.PinRefereeToGameAssignmentChange;
import org.junit.jupiter.api.*;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolverManager;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class GameAssignmentProblemChangeTests {
    
    @Inject
    SolverManager<RefereeTimeTable, Long> solverManager;
    @Inject
    ScoreManager<RefereeTimeTable, HardSoftScore> scoreManager;
    
    @Test
    @Timeout(unit = TimeUnit.MINUTES, value = 2)
    void pinRefereeChangeTest() throws InterruptedException {
        TimeTableGenerator timeTableGenerator = new TimeTableGenerator().amountOfGames(300).amountOfReferees(900);
        RefereeTimeTable intermediateTimeTable = timeTableGenerator.build();
        UUID refereeUUID = intermediateTimeTable.getReferees().get(0).getRefereeUUID();
        UUID gameAssignmentUUID = intermediateTimeTable.getGameAssignments().get(0).getAssignmentUUID();
    
        RefereeTimeTable refereeTimeTable = new ProblemChangeSolver().refereeTimeTableProblemChangeSolver(solverManager, scoreManager, "local/solverOutput/availabilityProblemChangeTests",
                PinRefereeToGameAssignmentChange.builder().assignmentUUID(gameAssignmentUUID).refereeUUID(refereeUUID).build(),
                intermediateTimeTable.toBuilder()
                        .build(), timeTableGenerator);
        Referee adaptedReferee = refereeTimeTable.getReferees().stream().filter(referee1 -> referee1.getRefereeUUID().equals(refereeUUID)).findFirst().orElseThrow();
        assertTrue(adaptedReferee.getAssignments().stream().anyMatch(assignment -> assignment.getAssignmentUUID().equals(gameAssignmentUUID)));
    }
}
