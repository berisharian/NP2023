package vtor_kol;

import java.util.*;
import java.util.stream.IntStream;

class SeatTakenException extends Exception{
    public SeatTakenException() {
    }
}
class SeatNotAllowedException extends Exception{
    public SeatNotAllowedException() {
    }
}

class Sector{
    String code;
    int places;
    Map<Integer, Integer> map;

    public Sector(String code, int places) {
        this.code = code;
        this.places = places;
        this.map=new HashMap<>();
    }

    public boolean isSeatTaken(int seat){
        return map.containsKey(seat);
    }
    public boolean containsValue(int value){
        return map.containsValue(value);
    }
    public void buyTicket(int seat, int type){
        map.put(seat, type);
    }
    public int availableSeats(){
        return places - map.size();
    }

    public String getCode() {
        return code;
    }

    private double getPercent() {
        return map.size() / (double)places * 100.0;
    }

    @Override
    public String toString() {
        return String.format("%s\t%d/%d\t%.1f%%", code, availableSeats(), places, getPercent());
    }
}


class Stadium{
    String name;
    Map<String, Sector> sectors;

    public Stadium(String name) {
        this.name = name;
        this.sectors= new HashMap<>();
    }

    public void createSectors(String[] sectorNames, int[] sectorSizes) {
        IntStream.range(0, sectorNames.length)
                .forEach(x-> sectors.put(sectorNames[x], new Sector(sectorNames[x], sectorSizes[x])));
    }

    public void buyTicket(String sectorName, int seat, int type) throws SeatTakenException, SeatNotAllowedException {
        if(sectors.get(sectorName).isSeatTaken(seat)){
            throw new SeatTakenException();
        }
        if(type !=0 && sectors.get(sectorName).containsValue(((type -1 )^1) +1 )){
            throw new SeatNotAllowedException();
        }
        sectors.get(sectorName).buyTicket(seat, type);
    }

    public void showSectors() {
        sectors.values().stream()
                .sorted(Comparator.comparing(Sector::availableSeats).reversed().thenComparing(Sector::getCode))
                .forEach(System.out::println);
    }
}

public class StaduimTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] sectorNames = new String[n];
        int[] sectorSizes = new int[n];
        String name = scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            sectorNames[i] = parts[0];
            sectorSizes[i] = Integer.parseInt(parts[1]);
        }
        Stadium stadium = new Stadium(name);
        stadium.createSectors(sectorNames, sectorSizes);
        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            try {
                stadium.buyTicket(parts[0], Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]));
            } catch (SeatNotAllowedException e) {
                System.out.println("SeatNotAllowedException");
            } catch (SeatTakenException e) {
                System.out.println("SeatTakenException");
            }
        }
        stadium.showSectors();
    }
}

