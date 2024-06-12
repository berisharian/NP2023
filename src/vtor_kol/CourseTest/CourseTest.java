package vtor_kol.CourseTest;

//package mk.ukim.finki.midterm;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

interface UpdateFunction{
    void updatePoints(Student s, int points) throws Exception;
}

class Student{
    String id;
    String name;
    int firstMidtermPoints;
    int secondMidtermPoints;
    int labPoints;

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getFirstMidtermPoints() {
        return firstMidtermPoints;
    }

    public int getSecondMidtermPoints() {
        return secondMidtermPoints;
    }

    public int getLabPoints() {
        return labPoints;
    }
    public double getSummaryPoints(){
        return firstMidtermPoints * 0.45 + secondMidtermPoints *0.45 + labPoints;
    }

    public int grade(){
        int finalGrade=(int) getSummaryPoints()/10 +1;
        if(finalGrade>10)
            return 10;
        if (finalGrade<6)
            return 5;
        return finalGrade;
    }

    @Override
    public String toString() {
        //ID: 151020 Name: Stefan First midterm: 78 Second midterm 80 Labs: 8 Summary points: 79.10 Grade: 8
        return String.format("ID: %s Name: %s First midterm: %d Second midterm %d Labs: %d Summary points: %.2f Grade: %d",
                id, name, firstMidtermPoints, secondMidtermPoints, labPoints, getSummaryPoints(), grade());
    }
}

class AdvancedProgrammingCourse{
    Map<String, Student> studentMap;
    Map<String, UpdateFunction> updateFunctionMap;

    public AdvancedProgrammingCourse() {
        studentMap=new HashMap<>();
        this.updateFunctionMap=new HashMap<>();

        updateFunctionMap.put("midterm1", (s, p) -> {
            if (p > 100) throw new Exception();
            s.firstMidtermPoints = p;
        });

        updateFunctionMap.put("midterm2", (s, p) -> {
            if (p > 100) throw new Exception();
            s.secondMidtermPoints = p;
        });

        updateFunctionMap.put("labs", (s, p) -> {
            if (p > 10) throw new Exception();
            s.labPoints = p;
        });
    }

    public void addStudent(Student student) {
        studentMap.put(student.id, student);
    }

    public void updateStudent(String idNumber, String activity, int points) {
        try {
            updateFunctionMap.get(activity).updatePoints(studentMap.get(idNumber), points);
        } catch (Exception e) {
            //DO NOTHING
        }
    }

    public List<Student> getFirstNStudents(int n) {
        return studentMap.values().stream()
                .sorted(Comparator.comparing(Student::getSummaryPoints).reversed())
                .limit(n).collect(Collectors.toList());
    }

    public Map<Integer, Integer> getGradeDistribution() {
        Map<Integer, Integer> result=studentMap.values().stream()
                .map(Student::grade)
                .collect(Collectors.groupingBy(
                        grade->grade,
                        TreeMap::new,
                        Collectors.summingInt(i->1)
                ));
        IntStream.range(5,11).forEach(i->result.putIfAbsent(i, 0));
        return result;
    }

    public void printStatistics() {
            DoubleSummaryStatistics ds = studentMap.values()
                    .stream()
                    .filter(i->i.getSummaryPoints()>=50)
                    .mapToDouble(Student::getSummaryPoints)
                    .summaryStatistics();
            //Count: 1 Min: 79.10 Average: 79.10 Max: 79.10
            System.out.printf("Count: %d Min: %.2f Average: %.2f Max: %.2f",
                    ds.getCount(), ds.getMin(), ds.getAverage(), ds.getMax());
    }
}


public class CourseTest {

    public static void printStudents(List<Student> students) {
        students.forEach(System.out::println);
    }

    public static void printMap(Map<Integer, Integer> map) {
        map.forEach((k, v) -> System.out.printf("%d -> %d%n", k, v));
    }

    public static void main(String[] args) {
        AdvancedProgrammingCourse advancedProgrammingCourse = new AdvancedProgrammingCourse();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            String command = parts[0];

            if (command.equals("addStudent")) {
                String id = parts[1];
                String name = parts[2];
                advancedProgrammingCourse.addStudent(new Student(id, name));
            } else if (command.equals("updateStudent")) {
                String idNumber = parts[1];
                String activity = parts[2];
                int points = Integer.parseInt(parts[3]);
                advancedProgrammingCourse.updateStudent(idNumber, activity, points);
            } else if (command.equals("getFirstNStudents")) {
                int n = Integer.parseInt(parts[1]);
                printStudents(advancedProgrammingCourse.getFirstNStudents(n));
            } else if (command.equals("getGradeDistribution")) {
                printMap(advancedProgrammingCourse.getGradeDistribution());
            } else {
                advancedProgrammingCourse.printStatistics();
            }
        }
    }
}

