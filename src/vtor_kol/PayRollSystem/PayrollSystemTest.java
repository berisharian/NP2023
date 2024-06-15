package vtor_kol.PayRollSystem;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

abstract class Employee implements Comparable<Employee>{
    String id;
    String level;
    double rating;

    public Employee(String id, String level, double rating) {
        this.id = id;
        this.level = level;
        this.rating = rating;
    }

    public String getLevel() {
        return level;
    }

    public abstract double getSalary();

    @Override
    public int compareTo(Employee o) {
        int sal = Double.compare(o.getSalary(), this.getSalary());
        if (sal == 0)
            return this.level.compareTo(o.level);
        return sal;
    }

    @Override
    public String toString() {
        return String.format("Employee ID: %s Level: %s Salary: %.2f", id, level, getSalary());
    }
}

class FreelanceEmployee extends Employee{
    List<Integer> ticketPoints;

    public FreelanceEmployee(String id, String level, double rating, List<Integer> ticketPoints) {
        super(id, level, rating);
        this.ticketPoints = ticketPoints;
    }

    @Override
    public double getSalary() {
        return ticketPoints.stream().mapToInt(i->i).sum() * rating;
    }

    @Override
    public String toString() {
        return String.format("%s Tickets count: %d Tickets points: %d",
                super.toString(), ticketPoints.size(), ticketPoints.stream().mapToInt(x->x).sum());
    }
}

class HourlyEmployee extends Employee{
    double hours;

    public HourlyEmployee(String id, String level, double rating, double hours) {
        super(id, level, rating);
        this.hours = hours;
    }
    public double getRegularHours(){
       return Math.min(hours, 40);
    }
    public double getOverTimeHours(){
        return Math.max(0, hours - 40);
    }

    @Override
    public double getSalary() {
        return getRegularHours()*rating + getOverTimeHours() * rating*1.5;
    }

    @Override
    public String toString() {
        DecimalFormat df= new DecimalFormat("0.00");
        return String.format("%s Regular hours: %s Overtime hours: %s",
                super.toString(), df.format(getRegularHours()), df.format(getOverTimeHours()));
    }
}

class PayrollSystem{
    List<Employee> employees;
    Map<String,Double> hourlyRateByLevel;
    Map<String,Double> ticketRateByLevel;

    public PayrollSystem(Map<String, Double> hourlyRateByLevel, Map<String, Double> ticketRateByLevel) {
        this.hourlyRateByLevel = hourlyRateByLevel;
        this.ticketRateByLevel = ticketRateByLevel;
        this.employees=new ArrayList<>();
    }

    public void readEmployees(InputStream in) {
        Scanner scanner=new Scanner(in);
        while(scanner.hasNextLine()){
            String [] parts = scanner.nextLine().split(";");

            Employee employee;
            String id = parts[1];
            String level = parts[2];
            if(parts[0].equals("F")){
                List<Integer> points = new ArrayList<>();
                IntStream.range(3, parts.length).forEach(i-> points.add(Integer.valueOf(parts[i])));
                employee = new FreelanceEmployee(id, level, ticketRateByLevel.get(level), points);
            }
            else {
                employee = new HourlyEmployee(id, level, hourlyRateByLevel.get(level), Double.parseDouble(parts[3]));
            }
            employees.add(employee);
        }
    }

    public Map<String, Set<Employee>> printEmployeesByLevels (OutputStream os, Set<String> levels){

        Map<String, Set<Employee>> res=new LinkedHashMap<>();

        levels.stream().sorted().forEach(level -> employees.stream().filter(x->x.getLevel().equals(level))
                .forEach(employee -> {
                    res.computeIfAbsent(level, x-> new HashSet<>());
                    res.put(level, employees.stream().filter(x->x.getLevel().equals(level))
                            .sorted().collect(Collectors.toCollection(LinkedHashSet::new)));
                }));

        return res;

    }
}

public class PayrollSystemTest {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 10 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5 + i * 2.5);
        }

        PayrollSystem payrollSystem = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);

        System.out.println("READING OF THE EMPLOYEES DATA");
        payrollSystem.readEmployees(System.in);

        System.out.println("PRINTING EMPLOYEES BY LEVEL");
        Set<String> levels = new LinkedHashSet<>();
        for (int i=5;i<=10;i++) {
            levels.add("level"+i);
        }
        Map<String, Set<Employee>> result = payrollSystem.printEmployeesByLevels(System.out, levels);
        result.forEach((level, employees) -> {
            System.out.println("LEVEL: "+ level);
            System.out.println("Employees: ");
            employees.forEach(System.out::println);
            System.out.println("------------");
        });


    }
}
