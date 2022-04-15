package me.superbiebel.referee_planner.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public class TimePeriod {
    @Getter
    private LocalDateTime start;
    @Getter
    private LocalDateTime end;
    
    public TimePeriod() {
    }
    
    public TimePeriod(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }
    
    public boolean doesOverLap(TimePeriod timeperiod) {
        return !start.isAfter(timeperiod.getEnd()) && !timeperiod.getStart().isAfter(end);
    }
}
