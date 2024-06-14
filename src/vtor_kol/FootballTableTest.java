package vtor_kol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Partial exam II 2016/2017
 */
class Team implements Comparable<Team>{
    int goals;
    int wins;
    int draws;
    int loses;

    public Team() {
        goals=wins=draws=loses=0;
    }
    public void update(int type){
        if (type>0)
            wins++;
        else if(type == 0)
            draws++;
        else
            loses++;
    }

    public void changeGoals(int g){
        goals +=g;
    }
    public int plays(){
       return wins+loses+draws;
    }
    public int pts(){
        return 3*wins + draws;
    }

    @Override
    public int compareTo(Team o) {
        return   Comparator.comparing(Team::pts).thenComparing(i->i.goals).compare(this, o);
    }


    @Override
    public String toString() {
        return String.format("%5d%5d%5d%5d%5d", plays(), wins, draws, loses, pts());
    }
}

class FootballTable {
    Map<String, Team> teams = new HashMap<>();

    public void addGame(String homeTeam, String awayTeam, int homeGoals, int awayGoals) {
            teams.putIfAbsent(homeTeam, new Team());
            teams.putIfAbsent(awayTeam, new Team());

            Team home = teams.get(homeTeam);
            Team away = teams.get(awayTeam);

            if(homeGoals> awayGoals){
                home.update(1);
                away.update(-1);
            }
            else if(homeGoals < awayGoals){
                home.update(-1);
                away.update(+1);
            }
            else {
                home.update(0);
                away.update(0);
            }

            home.changeGoals(homeGoals-awayGoals);
            away.changeGoals(awayGoals - homeGoals);
    }

    public void printTable() {
        Map<String, Team> sorted = teams.entrySet().stream()
                .sorted(Map.Entry.<String, Team> comparingByValue()
                        .thenComparing(Map.Entry.comparingByKey(Comparator.reverseOrder())).reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new
                ));
        int indx =1;
        for(String team: sorted.keySet()){
            System.out.printf("%2d. %-15s%s\n", indx, team, teams.get(team));
            indx++;
        }
    }
}

public class FootballTableTest {
    public static void main(String[] args) throws IOException {
        FootballTable table = new FootballTable();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.lines()
                .map(line -> line.split(";"))
                .forEach(parts -> table.addGame(parts[0], parts[1],
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])));
        reader.close();
        System.out.println("=== TABLE ===");
        System.out.printf("%-19s%5s%5s%5s%5s%5s\n", "Team", "P", "W", "D", "L", "PTS");
        table.printTable();
    }
}

// Your code here

