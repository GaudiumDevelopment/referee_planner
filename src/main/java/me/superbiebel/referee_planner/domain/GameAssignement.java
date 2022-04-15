package me.superbiebel.referee_planner.domain;


import lombok.Getter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.UUID;
@PlanningEntity
public class GameAssignement {
    @Getter
    private UUID gameUUID;
    @Getter
    private Game game;
    @Getter
    @PlanningVariable(valueRangeProviderRefs = "refereeList")
    private Referee referee;
    @Getter
    private int indexInGame;
    @Getter
    private TimePeriod gamePeriod;
}
