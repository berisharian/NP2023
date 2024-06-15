package vtor_kol.PayrollSystemTest2;


import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class BonusNotAllowedException extends Exception{
    public BonusNotAllowedException(String message) {
        super(message);
    }
}

enum BonusType{
    FIXED,
    PERCENT
}

abstract class Employee implements Comparable<Employee>{
    String id;
    String level;
    double rating;
    BonusType bonusType;
    double bonus;
    boolean hasBonus;

    public Employee(String id, String level, double rating) {
        this.id = id;
        this.level = level;
        this.rating = rating;
        hasBonus=false;
    }

    public String getLevel() {
        return level;
    }
    public void setBonus(BonusType bonusType, double bonus){
        this.bonusType=bonusType;
        this.bonus=bonus;
        hasBonus=true;
    }

    public BonusType getBonusType() {
        return bonusType;
    }

    public abstract double getSalary();

    public abstract double totalSalaryWithBonus();

    public abstract double getBonusPrice();

    @Override
    public int compareTo(Employee o) {
        int sal = Double.compare(o.getSalary(), this.getSalary());
        if (sal == 0)
            return this.level.compareTo(o.level);
        return sal;
    }

    public double getBonus() {
        return bonus;
    }
    public int getNumberOfPoints(){
        return 0;
    }

    @Override
    public String toString() {
        return String.format("Employee ID: %s Level: %s Salary: %.2f", id, level, totalSalaryWithBonus());
    }
}

class FreelanceEmployee extends Employee {
    List<Integer> ticketPoints;

    public FreelanceEmployee(String id, String level, double rating, List<Integer> ticketPoints) {
        super(id, level, rating);
        this.ticketPoints = ticketPoints;
    }

    public List<Integer> getTicketPoints() {
        return ticketPoints;
    }

    @Override
    public double getSalary() {
        return ticketPoints.stream().mapToInt(i->i).sum() * rating;
      //  return bonusType == BonusType.FIXED ? salary + bonus : salary * (1+bonus/100.0);
    }

    @Override
    public double totalSalaryWithBonus() {
        return bonusType == BonusType.FIXED ? getSalary() + bonus : getSalary() * (1 + bonus/100.0);
    }
    @Override
    public double getBonusPrice(){
        return bonusType==BonusType.FIXED? bonus: getSalary() - (getSalary() * (1 - bonus/100.0));
    }

    @Override
    public String toString() {
        if(hasBonus) {
            return String.format("%s Tickets count: %d Tickets points: %d Bonus: %.2f",
                    super.toString(), ticketPoints.size(), ticketPoints.stream().mapToInt(x -> x).sum(), getBonusPrice());
        } else{
            return String.format("%s Tickets count: %d Tickets points: %d",
                    super.toString(), ticketPoints.size(), ticketPoints.stream().mapToInt(x -> x).sum());
        }
    }

    @Override
    public int getNumberOfPoints() {
        return ticketPoints.size();
    }
}

class HourlyEmployee extends Employee {
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

    public double getOverTimeSalary(){
        return getOverTimeHours() * rating *1.5;
    }

    @Override
    public double getSalary() {
        return getRegularHours()*rating + getOverTimeHours() * rating*1.5;
       // return bonusType==BonusType.FIXED ? salary + bonus : salary * ( 1 + bonus/100.0);
    }
    @Override
    public double getBonusPrice(){
        return bonusType==BonusType.FIXED? bonus : getSalary() - (getSalary() * (1 - bonus/100.0));
    }

    @Override
    public double totalSalaryWithBonus() {
        return bonusType == BonusType.FIXED ? getSalary() + bonus : getSalary() * (1 + bonus/100.0);
    }

    @Override
    public String toString() {
        DecimalFormat df= new DecimalFormat("0.00");
        if (hasBonus){
            return String.format("%s Regular hours: %s Overtime hours: %s Bonus: %.2f",
                    super.toString(), df.format(getRegularHours()), df.format(getOverTimeHours()), getBonusPrice());
        }
        else{
            return String.format("%s Regular hours: %s Overtime hours: %s",
                    super.toString(), df.format(getRegularHours()), df.format(getOverTimeHours()));
        }

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

    public Employee createEmployee (String input) throws BonusNotAllowedException {
        String [] line = input.split(" ");
        String [] e = line[0].split(";");

        Employee employee;
        String id = e[1];
        String level = e[2];

        if(e[0].equals("F")) {
            List<Integer> points = new ArrayList<>();
            IntStream.range(3, e.length).forEach(i -> points.add(Integer.parseInt(e[i])));
            employee = new FreelanceEmployee(id, level, ticketRateByLevel.get(level), points);
        } else {
            employee = new HourlyEmployee(id, level, hourlyRateByLevel.get(level), Double.parseDouble(e[3]));
        }

        double value;
        BonusType type;
        if(line.length>1){
            boolean isPercent = line[1].contains("%");
            value= Double.parseDouble(isPercent ?  line[1].substring(0, line[1].length()-1) : line[1]);
            type= isPercent ? BonusType.PERCENT : BonusType.FIXED;

            if((isPercent && value > 20) || (!isPercent && value>1000)) {
                String message = isPercent ? String.format("Bonus of %.2f%% is not allowed", value)
                        : String.format("Bonus of %.0f$ is not allowed", value);
                throw new BonusNotAllowedException(message);
            }
            employee.setBonus(type, value);
        }
        employees.add(employee);
        return employee;
    }

    public Map<String, Double> getOvertimeSalaryForLevels () {
        return employees.stream().filter(e-> e instanceof HourlyEmployee)

                .collect(Collectors.groupingBy(
                Employee::getLevel,
                Collectors.summingDouble(e -> ((HourlyEmployee) e).getOverTimeSalary())
        ));
    }

    public void printStatisticsForOvertimeSalary() {
        DoubleSummaryStatistics ds = employees.stream().filter(e-> e instanceof HourlyEmployee)
                .mapToDouble(e -> ((HourlyEmployee) e).getOverTimeSalary())
                .summaryStatistics();
        System.out.printf("Statistics for overtime salary: Min: %.2f Average: %.2f Max: %.2f Sum: %.2f\n",
                ds.getMin(), ds.getAverage(), ds.getMax(), ds.getSum());
    }

//    public Map<String, Integer> ticketsDoneByLevel() {
//            return employees.stream().filter(e-> e instanceof FreelanceEmployee)
//                    .collect(Collectors.groupingBy(
//                            Employee::getLevel,
//                            Collectors.summingInt(e -> ((FreelanceEmployee) e)
//                                    .getTicketPoints().size())
//                    ));
//    }
    public Map<String, Integer> ticketsDoneByLevel() {
            return employees.stream().filter(e->e.getNumberOfPoints()>0)
                    .collect(Collectors.groupingBy(Employee::getLevel,
                            Collectors.summingInt(Employee::getNumberOfPoints)));
    }

    public Collection<Employee> getFirstNEmployeesByBonus (int n){
        return employees.stream()
                .sorted(Comparator.comparing(Employee::getBonusPrice).reversed())
                .limit(n).collect(Collectors.toList());
    }
}

public class PayrollSystemTest2 {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 11 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5.5 + i * 2.5);
        }

        Scanner sc = new Scanner(System.in);

        int employeesCount = Integer.parseInt(sc.nextLine());

        PayrollSystem ps = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);
        Employee emp = null;
        for (int i = 0; i < employeesCount; i++) {
            try {
                emp = ps.createEmployee(sc.nextLine());
            } catch (BonusNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        }

        int testCase = Integer.parseInt(sc.nextLine());

        switch (testCase) {
            case 1: //Testing createEmployee
                if (emp != null)
                    System.out.println(emp);
                break;
            case 2: //Testing getOvertimeSalaryForLevels()
                ps.getOvertimeSalaryForLevels().forEach((level, overtimeSalary) -> {
                    System.out.printf("Level: %s Overtime salary: %.2f\n", level, overtimeSalary);
                });
                break;
            case 3: //Testing printStatisticsForOvertimeSalary()
                ps.printStatisticsForOvertimeSalary();
                break;
            case 4: //Testing ticketsDoneByLevel
                ps.ticketsDoneByLevel().forEach((level, overtimeSalary) -> {
                    System.out.printf("Level: %s Tickets by level: %d\n", level, overtimeSalary);
                });
                break;
            case 5: //Testing getFirstNEmployeesByBonus (int n)
                ps.getFirstNEmployeesByBonus(Integer.parseInt(sc.nextLine())).forEach(System.out::println);
                break;
        }

    }
}