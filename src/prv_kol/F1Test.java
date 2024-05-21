package prv_kol;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Driver implements Comparable<Driver>{
    String name;
    int lap1;
    int lap2;
    int lap3;

    int bestLap;

    public Driver(String name, int lap1, int lap2, int lap3) {
        this.name = name;
        this.lap1 = lap1;
        this.lap2 = lap2;
        this.lap3 = lap3;
        this.bestLap = Math.min(Math.min(lap1, lap2), lap3);
    }
    public static int stringToTime(String time){
        String [] parts=time.split(":");
        return Integer.parseInt(parts[0])*1000*60
                + Integer.parseInt(parts[1])*1000
                + Integer.parseInt(parts[2]);
    }
    public static String timeToString(int time){
        int minutes=(time/60)/1000;
        int seconds=(time - minutes*60*1000) /1000;
        int mm=time%1000;
        return String.format("%d:%02d:%03d", minutes,seconds,mm);

    }

    @Override
    public String toString() {
        return String.format("%-10s%10s", name, timeToString(bestLap));
    }

    @Override
    public int compareTo(Driver o) {
        return Integer.compare(this.bestLap, o.bestLap);
    }
}

class DriverFactory{
    public static Driver create(String line){
        String [] parts=line.split("\\s+");
        String name=parts[0];
        String lap1=parts[1];
        String lap2=parts[2];
        String lap3=parts[3];

        return new Driver(name,
                Driver.stringToTime(lap1),
                Driver.stringToTime(lap2),
                Driver.stringToTime(lap3));
    }

}

class F1Race {
    List<Driver> drivers;

    public F1Race() {
        this.drivers= new ArrayList<>();
    }

    public void readResults(InputStream in) {
        drivers=new BufferedReader(new InputStreamReader(in))
                .lines()
                .map(DriverFactory::create)
                .collect(Collectors.toList());
    }

    public void printSorted(PrintStream out) {
        PrintWriter printWriter=new PrintWriter(out);

        drivers=drivers.stream().sorted().collect(Collectors.toList());

        IntStream.range(0, drivers.size())
                .mapToObj(i->String.format("%d. %s", i+1, drivers.get(i)))
                .forEach(printWriter::println);
        printWriter.flush();
    }

}


public class F1Test {

    public static void main(String[] args) {
        F1Race f1Race = new F1Race();
        f1Race.readResults(System.in);
        f1Race.printSorted(System.out);
    }

}
