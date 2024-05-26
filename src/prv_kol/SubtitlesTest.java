package prv_kol;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class SubtitleTime{
    private int hour;
    private int minute;
    private int second;
    private int millisecond;

    public SubtitleTime(int hour, int minute, int second, int millisecond) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.millisecond = millisecond;
    }

    //17
    //00:01:53,468 --> 00:01:54,745
    //All right.
    SubtitleTime(String pattern){
        if (pattern.charAt(0) == ' '){
            pattern=pattern.substring(1);
        }
        String [] time=pattern.split(",");
        millisecond=Integer.parseInt(time[1]);
        String [] std=time[0].split(":");
        hour=Integer.parseInt(std[0]);
        minute = Integer.parseInt(std[1]);
        second = Integer.parseInt(std[2]);
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public int getMillisecond() {
        return millisecond;
    }

    public void shift(int ms){
        millisecond += ms;
        if(millisecond > 999) {
            millisecond %=1000;
            second++;
        }
        else if (millisecond< 0){
            millisecond +=1000;
            second--;
        }
        if(second>59){
            second-=60;
            minute++;
        }
        else if( second<0){
            second+=60;
            minute--;
        }
        if(minute>59){
            minute-=60;
            hour++;
        }
        else if(minute<0){
            minute+=60;
            hour--;
        }
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d,%03d", hour, minute, second, millisecond);
    }
}

class Subtitle{
    private int index;
    private SubtitleTime from;
    private SubtitleTime to;
    private String text;

    public Subtitle(int index, SubtitleTime from, SubtitleTime to, String text) {
        this.index = index;
        this.from = from;
        this.to = to;
        this.text = text;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public SubtitleTime getFrom() {
        return from;
    }

    public void setFrom(SubtitleTime from) {
        this.from = from;
    }

    public SubtitleTime getTo() {
        return to;
    }

    public void setTo(SubtitleTime to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void shift(int ms){
        from.shift(ms);
        to.shift(ms);
    }

    @Override
    public String toString() {
        return String.format("%d\n%s --> %s\n%s", index, from, to, text);
    }
}

class Subtitles {
    List<Subtitle> arr;

    public Subtitles() {
        this.arr = new ArrayList<>();
    }

    public int loadSubtitles(InputStream in) {
        Scanner read= new Scanner(in);
        int loaded=0;

        while (read.hasNext()){
            int index= read.nextInt();
            String from = read.next();
            read.next();
            String to=read.nextLine();
            String text="";

            while (read.hasNextLine()){
                String add=read.nextLine();
                if (add.isEmpty())
                    break;
                text+=add + "\n";
            }
            arr.add(new Subtitle(index, new SubtitleTime(from), new SubtitleTime(to), text));
            loaded++;
        }
        return loaded;
    }

    public void print() {
        arr.forEach(System.out::println);
    }

    public void shift(int ms) {
        arr.forEach(i->i.shift(ms));
    }
}

public class SubtitlesTest {
    public static void main(String[] args) {
        Subtitles subtitles = new Subtitles();
        int n = subtitles.loadSubtitles(System.in);
        System.out.println("+++++ ORIGINIAL SUBTITLES +++++");
        subtitles.print();
        int shift = n * 37;
        shift = (shift % 2 == 1) ? -shift : shift;
        System.out.println(String.format("SHIFT FOR %d ms", shift));
        subtitles.shift(shift);
        System.out.println("+++++ SHIFTED SUBTITLES +++++");
        subtitles.print();
    }
}


// Вашиот код овде
