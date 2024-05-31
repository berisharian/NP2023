package vtor_kol;

import java.util.*;

class Candidate{
    private String city;
    private String code;
    private String name;
    private int age;

    public Candidate(String city, String code, String name, int age) {
        this.city = city;
        this.code = code;
        this.name = name;
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return String.format("%s %s %d", code, name, age);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candidate that = (Candidate) o;
        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}

class Audition{
    Map<String, Set<Candidate>> candidates;


    public Audition() {
        this.candidates=new HashMap<>();
    }
    public void addParticpant(String city, String code, String name, int age){
        Candidate c=new Candidate(city, code, name, age);
        if(!candidates.containsKey(city)){
            candidates.put(city, new HashSet<>());
        }
        candidates.get(city).add(c);
    }
    public void listByCity(String city){
        Set<Candidate> candidatesFromCity= candidates.get(city);
        if(candidatesFromCity.isEmpty()){
            System.out.println("NOT FOUND");
        }
        else{
            Comparator<Candidate> comparator=Comparator.comparing(Candidate::getName)
                    .thenComparing(Candidate::getAge).thenComparing(Candidate::getCode);
            candidatesFromCity.stream().sorted(comparator).forEach(System.out::println);
        }
    }
}

public class AuditionTest {
    public static void main(String[] args) {
        Audition audition = new Audition();
        List<String> cities = new ArrayList<String>();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            if (parts.length > 1) {
                audition.addParticpant(parts[0], parts[1], parts[2],
                        Integer.parseInt(parts[3]));
            } else {
                cities.add(line);
            }
        }
        for (String city : cities) {
            System.out.printf("+++++ %s +++++\n", city);
            audition.listByCity(city);
        }
        scanner.close();
    }
}
