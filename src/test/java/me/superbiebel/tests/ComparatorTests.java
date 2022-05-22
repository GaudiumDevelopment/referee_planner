package me.superbiebel.tests;

import io.quarkus.test.junit.QuarkusTest;
import me.superbiebel.referee_planner.domain.GameAssignment;
import me.superbiebel.referee_planner.domain.Referee;
import me.superbiebel.referee_planner.domain.TimePeriod;
import me.superbiebel.referee_planner.domain.comparators.GameAssignmentComparator;
import me.superbiebel.referee_planner.domain.comparators.RefereeStrengthComparator;
import me.superbiebel.referee_planner.domain.data.RandomDataGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ComparatorTests {
    
    @Test
    @Execution(ExecutionMode.CONCURRENT)
    void refereeStrengthComparatorTest() {
        Referee referee1 = Referee.builder().experience(10).build();
        Referee referee2 = Referee.builder().experience(20).build();
        
        assertTrue(new RefereeStrengthComparator().compare(referee1, referee2) < 0);
        assertTrue(new RefereeStrengthComparator().compare(referee2, referee1) > 0);
    }
    
    @Test
    @Execution(ExecutionMode.CONCURRENT)
    void GameAssignmentComparatorTest() {
        LocalDateTime testTime1 = RandomDataGenerator.BEGIN_GAME_DATE_TIME;
        GameAssignment assignment1 = GameAssignment.builder().game(RandomDataGenerator.generateGame()
                                                                           .toBuilder()
                                                                           .gamePeriod(TimePeriod.builder()
                                                                                               .start(testTime1)
                                                                                               .end(testTime1.plusHours(2))
                                                                                               .build())
                                                                           .build())
                                             .build();
        LocalDateTime testTime2 = RandomDataGenerator.BEGIN_GAME_DATE_TIME.plusHours(4);
        GameAssignment assignment2 = GameAssignment.builder().game(RandomDataGenerator.generateGame()
                                                                           .toBuilder()
                                                                           .gamePeriod(TimePeriod.builder()
                                                                                               .start(testTime2)
                                                                                               .end(testTime2.plusHours(2))
                                                                                               .build())
                                                                           .build())
                                             .indexInGame(0)
                                             .build();
        GameAssignment assignment3 = GameAssignment.builder().game(RandomDataGenerator.generateGame()
                                                                           .toBuilder()
                                                                           .gamePeriod(TimePeriod.builder()
                                                                                               .start(testTime2)
                                                                                               .end(testTime2.plusHours(2))
                                                                                               .build())
                                                                           .build())
                                             .indexInGame(1)
                                             .build();
        LocalDateTime testTime4 = RandomDataGenerator.BEGIN_GAME_DATE_TIME.plusHours(8);
        GameAssignment assignment4 = GameAssignment.builder().game(RandomDataGenerator.generateGame()
                                                                           .toBuilder()
                                                                           .gamePeriod(TimePeriod.builder()
                                                                                               .start(testTime4)
                                                                                               .end(testTime4.plusHours(2))
                                                                                               .build())
                                                                           .build())
                                             .build();
        List<GameAssignment> assignments = new ArrayList<>();
        
        assignments.add(assignment2);
        
        assignments.add(assignment4);
        assignments.add(assignment1);
        assignments.add(assignment3);
        assignments.sort(GameAssignmentComparator.COMPARATOR);
        assertEquals(assignment1, assignments.get(0));
        assertEquals(assignment2, assignments.get(1));
        assertEquals(assignment3, assignments.get(2));
        assertEquals(assignment4, assignments.get(3));
    }
}
