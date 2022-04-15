package me.superbiebel.referee_planner.domain;

import lombok.Getter;

import java.time.LocalDateTime;

public class TimePeriod {
    @Getter
    private LocalDateTime start;
    @Getter
    private LocalDateTime end;
    
    public boolean doesOverLap(TimePeriod timeperiod) {
        return !start.isAfter(timeperiod.getEnd()) && !timeperiod.getStart().isAfter(end);
    }
}
