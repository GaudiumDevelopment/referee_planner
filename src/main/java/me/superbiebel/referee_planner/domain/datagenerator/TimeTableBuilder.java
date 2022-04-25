package me.superbiebel.referee_planner.domain.datagenerator;

import me.superbiebel.referee_planner.domain.Game;
import me.superbiebel.referee_planner.domain.GameAssignment;
import me.superbiebel.referee_planner.domain.Referee;
import me.superbiebel.referee_planner.domain.TimeTable;

import java.util.ArrayList;
import java.util.List;

public class TimeTableBuilder {
    
    private TimeTable.TimeTableBuilder timeTableBuilder = TimeTable.builder();
    
    public TimeTableBuilder amountOfReferees(int amount, boolean withNonExistent) {
        List<Referee> referees = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            referees.add(RandomDataGenerator.generateReferee());
        }
        referees.add(Referee.builder().isNonExist(true).build());
        timeTableBuilder.referees(referees);
        return this;
    }
    public TimeTableBuilder amountOfGames(int amount) {
        List<Game> games = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            games.add(RandomDataGenerator.generateGame());
        }
        timeTableBuilder.games(games);
        List<GameAssignment> assignments = new ArrayList<>();
        games.forEach(game-> assignments.addAll(RandomDataGenerator.generateGameAssignment(game)));
        timeTableBuilder.gameAssignments(assignments);
        return this;
    }
    public TimeTable build() {
        return timeTableBuilder.build();
    }
}
