package me.superbiebel.tests.problemchange;

import io.quarkus.test.junit.QuarkusTest;
import me.superbiebel.referee_planner.domain.Availability;
import me.superbiebel.referee_planner.domain.Referee;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import me.superbiebel.referee_planner.domain.data.RandomDataGenerator;
import me.superbiebel.referee_planner.domain.data.TimeTableGenerator;
import me.superbiebel.referee_planner.problemchanges.referee.RefereeAvailabilityChange;
import me.superbiebel.referee_planner.problemchanges.referee.RefereeExperienceChange;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolverManager;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class RefereeProblemChangeTests {
    @Inject
    SolverManager<RefereeTimeTable, Long> solverManager;
    @Inject
    ScoreManager<RefereeTimeTable, HardSoftScore> scoreManager;
    @Test
    @Execution(ExecutionMode.CONCURRENT)
    @Timeout(120)
    void availabilityChangeTest() throws InterruptedException {
        TimeTableGenerator timeTableGenerator = new TimeTableGenerator().amountOfGames(300).amountOfReferees(900);
        RefereeTimeTable intermediateTimeTable = timeTableGenerator.build();
        List<Referee> referees = new ArrayList<>(intermediateTimeTable.getReferees());
        
        UUID oldAvailabilityUUID = UUID.randomUUID();
        Availability oldAvailability = RandomDataGenerator
                                               .generateAvailabilityListForReferee(1)
                                               .get(0)
                                               .toBuilder()
                                               .availabilityUUID(oldAvailabilityUUID)
                                               .startLocation(RandomDataGenerator.giveLocationWithinBelgium())
                                               .build();
        UUID newAvailabilityUUID = UUID.randomUUID();
        Availability newAvailability = RandomDataGenerator
                                               .generateAvailabilityListForReferee(1)
                                               .get(0)
                                               .toBuilder()
                                               .availabilityUUID(newAvailabilityUUID)
                                               .startLocation(RandomDataGenerator.giveLocationWithinBelgium())
                                               .build();
        List<Availability> availabilityList = new ArrayList<>();
        availabilityList.add(oldAvailability);
        UUID refereeUUID = UUID.randomUUID();
        Referee referee = RandomDataGenerator.generateReferee()
                                  .toBuilder()
                                  .refereeUUID(refereeUUID)
                                  .availabilityList(availabilityList)
                                  .build();
        referees.add(referee);
        
        RefereeTimeTable refereeTimeTable = new ProblemChangeSolver().refereeTimeTableProblemChangeSolver(solverManager, scoreManager,"local/solverOutput/availabilityProblemChangeTests",
                RefereeAvailabilityChange.builder()
                        .newAvailability(newAvailability)
                        .oldAvailabilityUUID(oldAvailabilityUUID)
                        .refereeUUID(refereeUUID).build(),
                intermediateTimeTable.toBuilder()
                        .referees(referees)
                        .build(), timeTableGenerator);
        Referee adaptedReferee = refereeTimeTable.getReferees().stream().filter(referee1 -> referee1.getRefereeUUID().equals(refereeUUID)).findFirst().orElseThrow();
        assertEquals(newAvailabilityUUID, adaptedReferee.getAvailabilityList().get(0).getAvailabilityUUID());
    }
    @Test
    @Execution(ExecutionMode.CONCURRENT)
    @Timeout(120)
    void availabilityRemoveTest() throws InterruptedException {
        TimeTableGenerator timeTableGenerator = new TimeTableGenerator().amountOfGames(300).amountOfReferees(900);
        RefereeTimeTable intermediateTimeTable = timeTableGenerator.build();
        List<Referee> referees = new ArrayList<>(intermediateTimeTable.getReferees());
        
        UUID oldAvailabilityUUID = UUID.randomUUID();
        Availability oldAvailability = RandomDataGenerator
                                               .generateAvailabilityListForReferee(1)
                                               .get(0)
                                               .toBuilder()
                                               .availabilityUUID(oldAvailabilityUUID)
                                               .startLocation(RandomDataGenerator.giveLocationWithinBelgium())
                                               .build();
        UUID newAvailabilityUUID = UUID.randomUUID();
        Availability newAvailability = RandomDataGenerator
                                               .generateAvailabilityListForReferee(1)
                                               .get(0)
                                               .toBuilder()
                                               .availabilityUUID(newAvailabilityUUID)
                                               .startLocation(RandomDataGenerator.giveLocationWithinBelgium())
                                               .build();
        List<Availability> availabilityList = new ArrayList<>();
        availabilityList.add(oldAvailability);
        UUID refereeUUID = UUID.randomUUID();
        Referee referee = RandomDataGenerator.generateReferee()
                                  .toBuilder()
                                  .refereeUUID(refereeUUID)
                                  .availabilityList(availabilityList)
                                  .build();
        referees.add(referee);
        
        RefereeTimeTable refereeTimeTable = new ProblemChangeSolver().refereeTimeTableProblemChangeSolver(solverManager, scoreManager, "local/solverOutput/availabilityProblemChangeTests",
                RefereeAvailabilityChange.builder()
                        .newAvailability(newAvailability)
                        .oldAvailabilityUUID(oldAvailabilityUUID)
                        .refereeUUID(refereeUUID).build(),
                intermediateTimeTable.toBuilder()
                        .referees(referees)
                        .build(), timeTableGenerator);
        Referee adaptedReferee = refereeTimeTable.getReferees().stream().filter(referee1 -> referee1.getRefereeUUID().equals(refereeUUID)).findFirst().orElseThrow();
        assertEquals(newAvailabilityUUID, adaptedReferee.getAvailabilityList().get(0).getAvailabilityUUID());
    }
    @Test
    @Execution(ExecutionMode.CONCURRENT)
    @Timeout(120)
    void experienceChangeTest() throws InterruptedException {
        TimeTableGenerator timeTableGenerator = new TimeTableGenerator().amountOfGames(300).amountOfReferees(900);
        RefereeTimeTable intermediateTimeTable = timeTableGenerator.build();
        List<Referee> referees = new ArrayList<>(intermediateTimeTable.getReferees());
        int adaptedExperience = 90;
        UUID refereeUUID = UUID.randomUUID();
        Referee referee = RandomDataGenerator.generateReferee()
                                  .toBuilder()
                                  .refereeUUID(refereeUUID)
                                  .build();
        referees.add(referee);
        RefereeTimeTable refereeTimeTable = new ProblemChangeSolver().refereeTimeTableProblemChangeSolver(solverManager, scoreManager, "local/solverOutput/availabilityProblemChangeTests",
                RefereeExperienceChange.builder().refereeUUID(refereeUUID).newExperience(adaptedExperience).build(),
                intermediateTimeTable.toBuilder()
                        .referees(referees)
                        .build(), timeTableGenerator);
        Referee adaptedReferee = refereeTimeTable.getReferees().stream().filter(referee1 -> referee1.getRefereeUUID().equals(refereeUUID)).findFirst().orElseThrow();
        assertEquals(adaptedExperience, adaptedReferee.getExperience());
    }
}
