package prv_kol.RiskTester;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Round{
    List<Integer> attacker;
    List<Integer> defender;

    public Round(List<Integer> attacker, List<Integer> defender) {
        this.attacker = attacker;
        this.defender = defender;
    }
    public Round(String line){
        String [] parts=line.split(";");

        this.attacker= parseDice(parts[0]);

        this.defender=parseDice(parts[1]);
    }
    private List<Integer> parseDice(String input){
        return Arrays.stream(input.split("\\s+"))
                .map(Integer::parseInt)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }
    public boolean hasAttackedWin(){
        return IntStream.range(0, attacker.size())
                .allMatch(i->attacker.get(i) > defender.get(i));

//        for(int i=0;i<attacker.size();i++){
//            if(attacker.get(i) < defender.get(i))
//                return false;
//        }
//        return true;
    }
}

class Risk{

    public int processAttacksData(InputStream in) {
        BufferedReader br=new BufferedReader(new InputStreamReader(in));

        return  (int) br.lines()
                .map(Round::new)
                .filter(Round::hasAttackedWin)
                .count();

    }
}

public class RiskTester {
    public static void main(String[] args) {

        Risk risk = new Risk();

        System.out.println(risk.processAttacksData(System.in));

    }
}