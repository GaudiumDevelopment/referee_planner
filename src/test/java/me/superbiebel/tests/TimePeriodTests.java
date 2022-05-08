package me.superbiebel.tests;

import io.quarkus.test.junit.QuarkusTest;
import me.superbiebel.referee_planner.domain.TimePeriod;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class TimePeriodTests {
    
    @Test
    @Execution(ExecutionMode.CONCURRENT)
    void overlapTestBegin() {
        TimePeriod timePeriod1 = TimePeriod.builder()
                                         .start(LocalDateTime.now().minusDays(3))
                                         .end(LocalDateTime.now().minusDays(1))
                                         .build();
        TimePeriod timePeriod2 = TimePeriod.builder()
                                         .start(LocalDateTime.now().minusDays(4))
                                         .end(LocalDateTime.now().minusDays(0))
                                         .build();
        assertTrue(timePeriod1.doesOverLap(timePeriod2));
    }
    
    @Test
    @Execution(ExecutionMode.CONCURRENT)
    void overlapTestEnd() {
        TimePeriod timePeriod1 = TimePeriod.builder()
                                         .start(LocalDateTime.now().minusDays(3))
                                         .end(LocalDateTime.now().minusDays(1))
                                         .build();
        TimePeriod timePeriod2 = TimePeriod.builder()
                                         .start(LocalDateTime.now().minusDays(2))
                                         .end(LocalDateTime.now().minusDays(0))
                                         .build();
        assertTrue(timePeriod1.doesOverLap(timePeriod2));
    }
    
    @Test
    @Execution(ExecutionMode.CONCURRENT)
    void encompassedByTest() {
        TimePeriod timePeriod1 = TimePeriod.builder()
                                         .start(LocalDateTime.now().minusDays(3))
                                         .end(LocalDateTime.now().minusDays(1))
                                         .build();
        TimePeriod timePeriod2 = TimePeriod.builder()
                                         .start(LocalDateTime.now().minusDays(4))
                                         .end(LocalDateTime.now().minusDays(0))
                                         .build();
        assertTrue(timePeriod1.encompassedBy(timePeriod2));
    }
    
    @Test
    @Execution(ExecutionMode.CONCURRENT)
    void doesEncompassTest() {
        TimePeriod timePeriod1 = TimePeriod.builder()
                                         .start(LocalDateTime.now().minusDays(4))
                                         .end(LocalDateTime.now().minusDays(0))
                                         .build();
        TimePeriod timePeriod2 = TimePeriod.builder()
                                         .start(LocalDateTime.now().minusDays(3))
                                         .end(LocalDateTime.now().minusDays(1))
                                         .build();
        assertTrue(timePeriod1.doesEncompass(timePeriod2));
    }
    
    @Test
    @Execution(ExecutionMode.CONCURRENT)
    void compactAndSortTimePeriods() {
        TimePeriod timePeriod1 = TimePeriod.builder()
                                         .start(LocalDateTime.now().minusDays(32))
                                         .end(LocalDateTime.now().minusDays(24))
                                         .build();
        TimePeriod timePeriod2 = TimePeriod.builder()
                                         .start(LocalDateTime.now().minusDays(28))
                                         .end(LocalDateTime.now().minusDays(20))
                                         .build();
        TimePeriod timePeriod3 = TimePeriod.builder()
                                         .start(LocalDateTime.now().minusDays(40))
                                         .end(LocalDateTime.now().minusDays(36))
                                         .build();
        TimePeriod timePeriod4 = TimePeriod.builder()
                                         .start(LocalDateTime.now().minusDays(16))
                                         .end(LocalDateTime.now().minusDays(12))
                                         .build();
        TimePeriod timePeriod5 = TimePeriod.builder()
                                         .start(LocalDateTime.now().minusDays(22))
                                         .end(LocalDateTime.now().minusDays(18))
                                         .build();
        //order 3,(1,2,5),4
        List<TimePeriod> timePeriodList = new ArrayList<>(4);
        timePeriodList.add(timePeriod1);
        timePeriodList.add(timePeriod2);
        timePeriodList.add(timePeriod3);
        timePeriodList.add(timePeriod4);
        timePeriodList.add(timePeriod5);
        List<TimePeriod> sortedTimePeriodList = TimePeriod.compactAndSortTimePeriods(timePeriodList);
        assertEquals(timePeriod3, sortedTimePeriodList.get(0));
        assertEquals(3, sortedTimePeriodList.size());
        assertEquals(timePeriod1.getStart(), sortedTimePeriodList.get(1).getStart());
        assertEquals(timePeriod5.getEnd(), sortedTimePeriodList.get(1).getEnd());
    }
}
