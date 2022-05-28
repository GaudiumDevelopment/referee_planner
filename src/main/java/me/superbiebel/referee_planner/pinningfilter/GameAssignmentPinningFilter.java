package me.superbiebel.referee_planner.pinningfilter;

import me.superbiebel.referee_planner.domain.GameAssignment;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;
import org.optaplanner.core.api.domain.entity.PinningFilter;

public class GameAssignmentPinningFilter implements PinningFilter<RefereeTimeTable, GameAssignment> {
    @Override
    public boolean accept(RefereeTimeTable refereeTimeTable, GameAssignment gameAssignment) {
        return gameAssignment.isPinnedReferee();
    }
}
