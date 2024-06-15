package vtor_kol.StudentRecords;

import com.sun.source.tree.Tree;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * January 2016 Exam problem 1
 */

class Records implements Comparable<Records> {
    String code;
    String direction;
    List<Integer> grades;

    public Records(String code, String direction, List<Integer> grades) {
        this.code = code;
        this.direction = direction;
        this.grades = grades;
    }

    public List<Integer> getGrades() {
        return grades;
    }
    public double averageGrades(){
        return grades.stream().mapToDouble(i->i).average().orElse(0);
    }

    public String getCode() {
        return code;
    }

    @Override
    public int compareTo(Records o) {
       return Comparator.comparing(Records::averageGrades).reversed()
               .thenComparing(Records::getCode).compare(this, o);
    }

    @Override
    public String toString() {
        return String.format("%s %.2f", code, averageGrades());
    }
}



class StudentRecords{

    Map<String, Set<Records>> records= new TreeMap<>();

    public int readRecords(InputStream in) {
        Scanner scanner=new Scanner(in);
        int count=0;
        while(scanner.hasNextLine()){
            String [] parts= scanner.nextLine().split(" ");
            List<Integer> grades= new ArrayList<>();
            String code = parts[0];
            String direction = parts[1];
            IntStream.range(2, parts.length).forEach(i-> grades.add(Integer.parseInt(parts[i])));
            Records record = new Records(code, direction, grades);

            records.computeIfAbsent(direction, x-> new HashSet<>());
            records.computeIfPresent(direction, (k, v) -> {v.add(record); return v;});
            count++;
        }
        return count;

    }

    public void writeTable(PrintStream out) {
        PrintWriter pw= new PrintWriter(out);

        records.forEach((k, v) -> {
            pw.println(k);
            v.stream().sorted().forEach(pw::println);
        });

        pw.flush();
    }

    public void writeDistribution(PrintStream out) {
        PrintWriter printWriter=new PrintWriter(out);

        records.entrySet().stream()
                        .sorted(Comparator.comparing(x-> -getGrades(x.getKey(), 10)))
                                .forEach(x-> {
                                    System.out.println(x.getKey());
                                    IntStream.range(6, 11).forEach(i->{
                                        int ct = (int) getGrades(x.getKey(), i );
                                        System.out.printf("%2d | %s(%d)\n", i, "*".repeat((int) Math.ceil((double) ct/10)), ct);
                                    });
                                });

        printWriter.flush();
    }

    public long getGrades(String k, int n){
        return records.get(k).stream()
                .map(Records::getGrades)
                .flatMap(Collection::stream)
                .filter(grade->grade == n).count();
    }
}

public class StudentRecordsTest {
    public static void main(String[] args) {
        System.out.println("=== READING RECORDS ===");
        StudentRecords studentRecords = new StudentRecords();
        int total = studentRecords.readRecords(System.in);
        System.out.printf("Total records: %d\n", total);
        System.out.println("=== WRITING TABLE ===");
        studentRecords.writeTable(System.out);
        System.out.println("=== WRITING DISTRIBUTION ===");
        studentRecords.writeDistribution(System.out);
    }
}

// your code here
