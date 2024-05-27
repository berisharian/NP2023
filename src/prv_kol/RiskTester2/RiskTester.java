package prv_kol.RiskTester2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Round{
    List<Integer> attackers;
    List<Integer> defenders;

    public Round(List<Integer> attackers, List<Integer> defenders) {
        this.attackers = attackers;
        this.defenders = defenders;
    }
    public Round(String line){
        String [] parts=line.split(";");
        this.attackers=parseDice(parts[0]);
        this.defenders=parseDice(parts[1]);
    }

    public List<Integer> parseDice(String part) {
        return Arrays.stream(part.split("\\s+"))
                .map(Integer::parseInt)
                .sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        int countA=0;
        int countD=0;
        for(int i=0; i < attackers.size(); i++){
            if(attackers.get(i) > defenders.get(i))
                countA++;
            else
                countD++;
        }
        return String.format("%d %d", countA, countD);
    }
}

class Risk{

    public void processAttacksData(InputStream in) {
        BufferedReader br= new BufferedReader(new InputStreamReader(in));
        br.lines()
                .map(Round::new)
                .forEach(System.out::println);
    }
}

public class RiskTester {
    public static void main(String[] args) {
        Risk risk = new Risk();
        risk.processAttacksData(System.in);
    }
}