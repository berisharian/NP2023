package prv_kol;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class UnsupportedFormatException extends Exception{
    public UnsupportedFormatException(String message) {
        super(message);
    }
}

class InvalidTimeException extends Exception{
    public InvalidTimeException(String message) {
        super(message);
    }
}

class Time implements Comparable<Time>{
    private int hour;
    private int minute;

    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public Time() {
    }

    public String getTime(TimeFormat format){
        StringBuilder sb=new StringBuilder();
        if(format == TimeFormat.FORMAT_24){
            sb.append(String.format("%2d:%02d", hour, minute));
        }
        if(format == TimeFormat.FORMAT_AMPM){
            boolean isPm= hour>=12 && hour <=23;
            if(hour == 0 && minute >= 0 && minute <=59){
                sb.append(String.format("%2d:%02d AM", hour + 12, minute));
            }
            else {
                sb.append(String.format("%2d:%02d %s", isPm && hour>12 ? hour % 12: hour, minute,
                isPm? "PM" : "AM"));
            }
        }
        return sb.toString();
    }

    @Override
    public int compareTo(Time o) {
        if(this.hour !=o.hour)
            return Integer.compare(this.hour, o.hour);
        return Integer.compare(this.minute, o.minute);
    }
}

class TimeTable{
    List<Time> times;

    public TimeTable() {
        this.times = new ArrayList<>();
    }

    public void readTimes(InputStream in) throws UnsupportedFormatException, InvalidTimeException {
        Scanner input = new Scanner(in);
        while(input.hasNext()){
            String time=input.next();
            if(!time.contains(".") && !time.contains(":"))
                throw new UnsupportedFormatException(time);

            String character=":";
            if(time.contains("."))
                character="\\.";

            String [] time2= time.split(character);
            int hour=Integer.parseInt(time2[0]);
            int minute=Integer.parseInt(time2[1]);
            if((hour>23 || hour< 0) && (minute < 0 || minute >59))
                throw new InvalidTimeException(time);
            times.add(new Time(hour, minute));
        }

    }

    public void writeTimes(PrintStream out, TimeFormat format24) {
        PrintWriter printWriter=new PrintWriter(out);
        times.stream().sorted().forEach(i->printWriter.println(i.getTime(format24)));
        printWriter.flush();
    }
}

public class TimesTest {

    public static void main(String[] args) {
        TimeTable timeTable = new TimeTable();
        try {
            timeTable.readTimes(System.in);
        } catch (UnsupportedFormatException e) {
            System.out.println("UnsupportedFormatException: " + e.getMessage());
        } catch (InvalidTimeException e) {
            System.out.println("InvalidTimeException: " + e.getMessage());
        }
        System.out.println("24 HOUR FORMAT");
        timeTable.writeTimes(System.out, TimeFormat.FORMAT_24);
        System.out.println("AM/PM FORMAT");
        timeTable.writeTimes(System.out, TimeFormat.FORMAT_AMPM);
    }

}

enum TimeFormat {
    FORMAT_24, FORMAT_AMPM
}