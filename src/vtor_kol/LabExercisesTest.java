package vtor_kol;

import java.util.*;
import java.util.stream.Collectors;

class Student {
    String index;
    static int COUNT_OF_LABS = 10;
    List<Integer> labPoints;
    public Student(String index, List<Integer> labPoints) {
        this.index = index;
        this.labPoints = labPoints;
    }

    public double getAverage(){
        return labPoints.stream().mapToInt(i -> i).sum()/(double) COUNT_OF_LABS;
    }

    public String getIndex() {
        return index;
    }

    public static int getCountOfLabs() {
        return COUNT_OF_LABS;
    }

    public List<Integer> getLabPoints() {
        return labPoints;
    }
    public boolean hasSignature(){
        return labPoints.size() >= 8;
    }
    public int getYearOfStudies(){
        return 20 - Integer.parseInt(index.substring(0, 2));
    }

    @Override
    public String toString() {
        return String.format("%s %s %.2f", index, hasSignature()? "YES" : "NO", getAverage());
    }
}

class LabExercises{
    List<Student> students;

    public LabExercises(){
        this.students = new ArrayList<>();
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void printByAveragePoints(boolean ascending, int n) {
        Comparator <Student> asc = Comparator.comparing(Student::getAverage).thenComparing(s -> s.index);
        if(!ascending){
            asc= asc.reversed();
        }
        students.stream()
                    .sorted(asc)
                    .limit(n)
                    .forEach(System.out::println);
    }

    public List<Student> failedStudents() {
        Comparator<Student> comparator = Comparator.comparing(Student::getIndex)
                .thenComparing(Student::getAverage);

        return students.stream()
                .filter(s -> !s.hasSignature())
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public Map<Integer, Double> getStatisticsByYear() {
            return students.stream()
                    .filter(Student::hasSignature)
                    .collect(Collectors.groupingBy(
                            Student::getYearOfStudies,
                            Collectors.averagingDouble(Student::getAverage)
                    ));

    }
}

public class LabExercisesTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LabExercises labExercises = new LabExercises();
        while (sc.hasNextLine()) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");
            String index = parts[0];
            List<Integer> points = Arrays.stream(parts).skip(1)
                    .mapToInt(Integer::parseInt)
                    .boxed()
                    .collect(Collectors.toList());

            labExercises.addStudent(new Student(index, points));
        }

        System.out.println("===printByAveragePoints (ascending)===");
        labExercises.printByAveragePoints(true, 100);
        System.out.println("===printByAveragePoints (descending)===");
        labExercises.printByAveragePoints(false, 100);
        System.out.println("===failed students===");
        labExercises.failedStudents().forEach(System.out::println);
        System.out.println("===statistics by year");
        labExercises.getStatisticsByYear().entrySet().stream()
                .map(entry -> String.format("%d : %.2f", entry.getKey(), entry.getValue()))
                .forEach(System.out::println);

    }
}
