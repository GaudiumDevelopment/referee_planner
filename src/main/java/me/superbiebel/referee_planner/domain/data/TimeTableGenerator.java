package me.superbiebel.referee_planner.domain.data;

import me.superbiebel.referee_planner.domain.Game;
import me.superbiebel.referee_planner.domain.GameAssignment;
import me.superbiebel.referee_planner.domain.Referee;
import me.superbiebel.referee_planner.domain.RefereeTimeTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TimeTableGenerator {
    
    public static final Referee VIRTUAL_REFEREE = Referee.builder()
                                                          .isNonExist(true)
                                                          .refereeUUID(new UUID(0, 0))
                                                          .experience(0)
                                                          .availabilityList(Collections.emptyList())
                                                          .build();
    
    private final RefereeTimeTable.RefereeTimeTableBuilder timeTableBuilder = RefereeTimeTable.builder();
    
    public TimeTableGenerator amountOfReferees(int amount) {
        List<Referee> referees = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            referees.add(RandomDataGenerator.generateReferee());
        }
        referees.add(VIRTUAL_REFEREE);
        timeTableBuilder.referees(referees);
        return this;
    }
    
    public TimeTableGenerator amountOfGames(int amount) {
        List<Game> games = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            games.add(RandomDataGenerator.generateGame());
        }
        timeTableBuilder.games(games);
        List<GameAssignment> assignments = new ArrayList<>();
        games.forEach(game -> assignments.addAll(Game.generateGameAssignments(game)));
        timeTableBuilder.gameAssignments(assignments);
        return this;
    }
    
    public RefereeTimeTable build() {
        return timeTableBuilder
                       .refereeTimeTableUUID(UUID.randomUUID())
                       .build();
    }
}
