package me.superbiebel.referee_planner.domain.data;

import me.superbiebel.referee_planner.domain.Game;
import me.superbiebel.referee_planner.domain.GameAssignment;
import me.superbiebel.referee_planner.domain.Referee;
import me.superbiebel.referee_planner.domain.TimeTable;

import java.util.ArrayList;
import java.util.List;

public class TimeTableBuilder {
    
    private final TimeTable.TimeTableBuilder timeTableGenerator = TimeTable.builder();
    
    public TimeTableBuilder amountOfReferees(int amount) {
        List<Referee> referees = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            referees.add(RandomDataGenerator.generateReferee());
        }
        referees.add(Referee.builder().isNonExist(true).build());
        timeTableGenerator.referees(referees);
        return this;
    }
    public TimeTableBuilder amountOfGames(int amount) {
        List<Game> games = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            games.add(RandomDataGenerator.generateGame());
        }
        timeTableGenerator.games(games);
        List<GameAssignment> assignments = new ArrayList<>();
        games.forEach(game-> assignments.addAll(RandomDataGenerator.generateGameAssignment(game)));
        timeTableGenerator.gameAssignments(assignments);
        return this;
    }
    public TimeTable build() {
        return timeTableGenerator.build();
    }
}
