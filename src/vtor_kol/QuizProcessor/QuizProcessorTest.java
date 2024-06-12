package vtor_kol.QuizProcessor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

class QuizAttempt{
    String studentId;
    List<String> correct;
    List<String> answers;

    public QuizAttempt(String studentId, List<String> correct, List<String> answers) {
        this.studentId = studentId;
        this.correct = correct;
        this.answers = answers;
    }
    public static QuizAttempt createAttempt(String line) throws Exception {
        String [] parts= line.split(";");
        String studentId= parts[0];
        List<String> correct = Arrays.stream(parts[1].split(", ")).collect(Collectors.toList());
        List<String> answer = Arrays.stream(parts[2].split(", ")).collect(Collectors.toList());
        if(correct.size() != answer.size())
            throw new Exception();
        return new QuizAttempt(studentId, correct, answer);
    }

    public double totalPoints(){
        double total=0;
        for(int i=0;i<correct.size(); i++){
            if(correct.get(i).equals(answers.get(i)))
                total+=1.0;
            else
                total-=0.25;
        }
        return total;
    }

    public String getStudentId() {
        return studentId;
    }
}

class QuizProcessor{

    public static Map<String, Double> processAnswers(InputStream in) {
        BufferedReader br=new BufferedReader(new InputStreamReader(in));
        List<QuizAttempt> quizAttempts= br
                .lines()
                .map(q -> {
                    try {
                        return QuizAttempt.createAttempt(q);
                    } catch (Exception e) {
                        System.out.println("A quiz must have same number of correct and selected answers");
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());
        return quizAttempts.stream().collect(Collectors.toMap(QuizAttempt::getStudentId, QuizAttempt::totalPoints, Double::sum, TreeMap::new));
    }
}

public class QuizProcessorTest {
    public static void main(String[] args) {
        QuizProcessor.processAnswers(System.in).forEach((k, v) -> System.out.printf("%s -> %.2f%n", k, v));
    }
}

