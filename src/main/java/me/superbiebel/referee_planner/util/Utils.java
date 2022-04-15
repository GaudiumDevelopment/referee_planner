package me.superbiebel.referee_planner.util;

import java.time.LocalDateTime;

public class Utils {
    public static boolean doesOverLap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }
}
