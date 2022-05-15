package me.superbiebel.tests;

import io.quarkus.test.junit.QuarkusTest;
import me.superbiebel.referee_planner.domain.Referee;
import me.superbiebel.referee_planner.domain.comparators.RefereeDifficultyComparator;
import me.superbiebel.referee_planner.domain.comparators.RefereeStrengthComparator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

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
    void refereeDifficultyComparatorTest() {
        Referee referee1 = Referee.builder().experience(10).build();
        Referee referee2 = Referee.builder().experience(20).build();
        
        assertTrue(new RefereeDifficultyComparator().compare(referee1, referee2) > 0);
        assertTrue(new RefereeDifficultyComparator().compare(referee2, referee1) < 0);
    }
}
