package vtor_kol;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class WrongDateException extends Exception{
    public WrongDateException(Date message) {
        super(String.format("Wrong date: %s", message));
    }
}

class Event implements Comparable<Event>{
    String name;
    String location;
    Date date;

    public Event(String name, String location, Date date) {
        this.name = name;
        this.location = location;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public int compareTo(Event o) {
       return Comparator.comparing(Event::getDate).thenComparing(Event::getName).compare(this, o);
    }

    @Override
    public String toString() {
        DateFormat dateFormat=new SimpleDateFormat("dd MMM, yyyy HH:mm");
        return String.format("%s at %s, %s", dateFormat.format(date), location, name);
    }

    public int getMonth(){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }
}

class EventCalendar{
    Set<Event> events;
    int year;

    public EventCalendar(int year) {
        this.year = year;
        this.events=new TreeSet<>();
    }

    public void addEvent(String name, String location, Date date) throws WrongDateException {
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(date);
            if(calendar.get(Calendar.YEAR) != year)
                throw new WrongDateException(date);
            events.add(new Event(name, location, date));
    }

    public void listEvents(Date date) {
        List<Event> filtered = events.stream().filter(x -> compareDates(x.date, date)).collect(Collectors.toList());
        if(filtered.isEmpty()) {
            System.out.println("No events on this day!");
            return;
        }
        filtered.forEach(System.out::println);
    }

    public void listByMonth() {
        IntStream.range(0, 12).forEach(i -> {
            long count = events.stream().filter(x -> x.getMonth() == i).count();
            System.out.printf("%d : %d%n", (i + 1), count);
        });
    }

    public boolean compareDates(Date date1, Date date2) {
        Calendar a = Calendar.getInstance(), b = Calendar.getInstance();
        a.setTime(date1);
        b.setTime(date2);

        return a.get(Calendar.DAY_OF_MONTH) == b.get(Calendar.DAY_OF_MONTH) &&
                a.get(Calendar.MONTH) == b.get(Calendar.MONTH) &&
                a.get(Calendar.YEAR) == b.get(Calendar.YEAR);
    }

}

public class EventCalendarTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        int year = scanner.nextInt();
        scanner.nextLine();
        EventCalendar eventCalendar = new EventCalendar(year);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            String name = parts[0];
            String location = parts[1];
            Date date = df.parse(parts[2]);
            try {
                eventCalendar.addEvent(name, location, date);
            } catch (WrongDateException e) {
                System.out.println(e.getMessage());
            }
        }
        Date date = df.parse(scanner.nextLine());
        eventCalendar.listEvents(date);
        eventCalendar.listByMonth();
    }
}

// vashiot kod ovde