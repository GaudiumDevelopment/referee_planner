package me.superbiebel.referee_planner.domain;

import lombok.Getter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@PlanningEntity
public class Referee {
    @Getter
    private UUID uuid;
    @Getter
    private int experience;
    @Getter
    private boolean isNonExist;
    
    private List<GameAssignement> assignments;
    
    private List<GameAssignement> getAssignments() {
        return assignments == null ? new ArrayList<GameAssignement>() {
            public boolean add(GameAssignement mt) {
                super.add(mt);
                this.sort(new GameAssignementComparator());
                return true;
            }
        }
        : assignments;
    }
}
