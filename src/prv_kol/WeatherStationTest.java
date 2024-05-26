package prv_kol;

import java.sql.Struct;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class Measurements implements Comparable<Measurements>{
   // temp, wind, hum, vis, date
    float temp, wind, hum, vis;
    Date date;

    public Measurements(float temp, float wind, float hum, float vis, Date date) {
        this.temp = temp;
        this.wind = wind;
        this.hum = hum;
        this.vis = vis;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        //22.1 18.9 km/h 1.3% 24.6 km Tue Dec 17 23:30:15 GMT 2013
        String dateToString= date.toString();
        dateToString=dateToString.replace("UTC", "GMT");
        return String.format("%.1f %.1f km/h %.1f%% %.1f km %s", temp, wind, hum, vis, dateToString);
    }

    public float getTemp() {
        return temp;
    }

    @Override
    public int compareTo(Measurements o) {
        return this.date.compareTo(o.date);
    }

}

class WeatherStation{
    int days;
    List<Measurements> measurements;

    public WeatherStation(int days) {
        this.days = days;
        this.measurements=new ArrayList<>();
    }

    public void addMeasurment(float temp, float wind, float hum, float vis, Date date) {
        Measurements m= new Measurements(temp, wind, hum, vis, date);
        if(measurements.isEmpty()){
            measurements.add(m);
        }
        Calendar now=Calendar.getInstance();
        now.setTime(date);
        Calendar lastTime= Calendar.getInstance();
        lastTime.setTime(measurements.get(measurements.size()-1).getDate());
        if(Math.abs(now.getTimeInMillis() - lastTime.getTimeInMillis()) < 2.5 * 60 * 1000)
            return;

        measurements.add(m);

        ArrayList<Measurements> toRemove=new ArrayList<>();
        for(Measurements day: measurements){
            Calendar c= Calendar.getInstance();
            c.setTime(day.getDate());
            if(Math.abs(now.getTimeInMillis() - c.getTimeInMillis()) > (long) days * 1000 * 60 * 60 * 24){
                toRemove.add(day);
            }
        }
        measurements.removeAll(toRemove);
    }

    public int total() {
        return measurements.size();
    }

    public void status(Date from, Date to) {
        ArrayList<Measurements> arr= new ArrayList<>();

        double sum=0.0;
        int count=0;
        for(Measurements m : measurements){
            Date d= m.getDate();
            if((d.after(from) || d.equals(from)) && (d.before(to) || d.equals(to))){
                arr.add(m);
                count++;
                sum+=m.getTemp();
            }
        }
        if(arr.isEmpty())
            throw new RuntimeException();

        StringBuilder sb=new StringBuilder();
        arr.forEach(x->sb.append(x).append("\n"));
        System.out.print(sb.toString());
        System.out.printf("Average temperature: %.2f", sum/count);
    }
}

public class WeatherStationTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        int n = scanner.nextInt();
        scanner.nextLine();
        WeatherStation ws = new WeatherStation(n);
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("=====")) {
                break;
            }
            String[] parts = line.split(" ");
            float temp = Float.parseFloat(parts[0]);
            float wind = Float.parseFloat(parts[1]);
            float hum = Float.parseFloat(parts[2]);
            float vis = Float.parseFloat(parts[3]);
            line = scanner.nextLine();
            Date date = df.parse(line);
            ws.addMeasurment(temp, wind, hum, vis, date);
        }
        String line = scanner.nextLine();
        Date from = df.parse(line);
        line = scanner.nextLine();
        Date to = df.parse(line);
        scanner.close();
        System.out.println(ws.total());
        try {
            ws.status(from, to);
        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }
}

// vashiot kod ovde
