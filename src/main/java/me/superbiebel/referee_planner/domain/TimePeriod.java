package me.superbiebel.referee_planner.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    
    public boolean encompassedBy(TimePeriod otherTimePeriod) {
        return otherTimePeriod.getStart().isBefore(start) && otherTimePeriod.getEnd().isAfter(end);
    }
    
    public boolean doesEncompass(TimePeriod otherTimePeriod) {
        return start.isBefore(otherTimePeriod.getStart()) && end.isAfter(otherTimePeriod.getEnd());
    }
    
    public static List<TimePeriod> compactAndSortTimePeriods(final List<TimePeriod> timePeriodList) {
        Comparator<TimePeriod> timePeriodComparator = (o1, o2) -> {
            if (o1.getStart().isBefore(o2.getStart())) {
                return -1;
            } else if (o1.getStart().isAfter(o2.getStart())) {
                return 1;
            } else if (o1.getStart().isEqual(o2.getStart())) {
                return 0;
            } else {
                throw new IllegalStateException("could not compare two game periods" + o1.getStart() + o2.getStart());
            }
        };
        
        List<TimePeriod> inputTimePeriods = new ArrayList<>(timePeriodList);
        inputTimePeriods.sort(timePeriodComparator);
        
        List<TimePeriod> resultTimePeriodList = new ArrayList<>();
        
        boolean adapted;
        do {
            adapted = false;
            for (int i = 0; i < inputTimePeriods.size(); i++) {
                if (inputTimePeriods.size() == i + 1) {
                    resultTimePeriodList.add(inputTimePeriods.get(i));
                    continue;
                }
                TimePeriod currentTimePeriod = inputTimePeriods.get(i);
                TimePeriod nextTimePeriod = inputTimePeriods.get(i + 1);
                if (currentTimePeriod.doesOverLap(nextTimePeriod)) {
                    adapted = true;
                    LocalDateTime begin;
                    LocalDateTime end;
                    if (currentTimePeriod.getStart().isBefore(nextTimePeriod.getStart())) {
                        begin = currentTimePeriod.getStart();
                    } else {
                        begin = nextTimePeriod.getStart();
                    }
                    if (currentTimePeriod.getEnd().isAfter(nextTimePeriod.getEnd())) {
                        end = currentTimePeriod.getEnd();
                    } else {
                        end = nextTimePeriod.getEnd();
                    }
                    resultTimePeriodList.add(TimePeriod.builder().start(begin).end(end).build());
                    inputTimePeriods.remove(i + 1);
                } else {
                    resultTimePeriodList.add(currentTimePeriod);
                }
                
            }
            inputTimePeriods = new ArrayList<>(resultTimePeriodList);
            resultTimePeriodList.clear();
        } while (adapted);
        return inputTimePeriods;
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        /*List<TimePeriod> betterTimePeriodList = new ArrayList<>(timePeriodList);
        betterTimePeriodList.sort(timePeriodComparator);
        List<TimePeriod> resultingTimePeriodList = new ArrayList<>();
        
        boolean adapted;
        boolean first = true;
        do {
            adapted = false;
            if (!first) {
                betterTimePeriodList = resultingTimePeriodList;
                resultingTimePeriodList = new ArrayList<>(betterTimePeriodList.size());
            }
            boolean previousWasCompacted = false;
            for (int i = 1; i < betterTimePeriodList.size(); i++) {
                TimePeriod currentTimePeriod = betterTimePeriodList.get(i);
                TimePeriod previousTimePeriod = betterTimePeriodList.get(i - 1);
                if (previousTimePeriod.doesOverLap(currentTimePeriod)) {
                    adapted = true;
                    previousWasCompacted = true;
                    resultingTimePeriodList.remove(resultingTimePeriodList.size()-2);
                    resultingTimePeriodList.add(TimePeriod.builder().start(previousTimePeriod.getStart()).end(currentTimePeriod.getEnd()).build());
                } else {
                    if (i == 1) {
                        resultingTimePeriodList.add(previousTimePeriod);
                    }
                    if (!previousWasCompacted) {
                        
                        resultingTimePeriodList.add(currentTimePeriod);
                    }
                    if (previousWasCompacted) previousWasCompacted = false;
                }
            }
            first = false;
        } while (adapted);
        return resultingTimePeriodList;*/
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TimePeriod{");
        sb.append("start=").append(start);
        sb.append(", end=").append(end);
        sb.append('}');
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        TimePeriod that = (TimePeriod) o;
        
        if (!getStart().equals(that.getStart())) return false;
        return getEnd().equals(that.getEnd());
    }
    
    @Override
    public int hashCode() {
        int result = getStart().hashCode();
        result = 31 * result + getEnd().hashCode();
        return result;
    }
}
