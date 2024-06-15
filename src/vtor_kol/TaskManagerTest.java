package vtor_kol;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

class DeadlineNotValidException extends Exception {
    DeadlineNotValidException(LocalDateTime deadline) {
        super("The deadline " + deadline + " has already passed");
    }
}

class Task {
    String name;
    String desc;
    LocalDateTime deadline;
    boolean hasDeadline = false;
    boolean hasPriority = false;
    int priority;

    public Task(String name, String desc) {
        this.name = name;
        this.desc = desc;
        this.deadline = LocalDateTime.now();
//        this.priority = priority;
    }
    public void setDeadline(LocalDateTime deadline) throws DeadlineNotValidException {
        LocalDateTime valid = LocalDateTime.parse("2020-06-02T00:00:00");
        if(deadline.isBefore(valid)){
            throw new DeadlineNotValidException(deadline);
        }
        this.deadline=deadline;
        this.hasDeadline=true;
    }

    public void setPriority(int priority) {
        this.priority = priority;
        hasPriority=true;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public int getPriority() {
        return priority;
    }
    public boolean isPriority() {
        return !hasPriority;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Task{");
        str.append("name='").append(name).append("', description='").append(desc).append("'");
        if(hasDeadline) str.append(", deadline=").append(deadline);
        if(hasPriority) str.append(", priority=").append(priority);
        str.append("}\n");
        return str.toString();
    }
}

class TaskManager {
    Map<String, List<Task>> tasks;

    public TaskManager() {
        this.tasks=new HashMap<>();
    }

    public void readTasks(InputStream inputStream) {
        Scanner scanner=new Scanner(inputStream);
        while(scanner.hasNextLine()){
            String [] line = scanner.nextLine().split(",");

            tasks.computeIfAbsent(line[0], x-> new ArrayList<>());
            Task task = new Task(line[1], line[2]);

            if(line.length>3){
                if (isValidDate(line[3])){
                    try {
                        task.setDeadline(LocalDateTime.parse(line[3]));
                    } catch (DeadlineNotValidException e) {
                        System.out.println(e.getMessage());
                        continue;
                    }
                    if(line.length==5)
                        task.setPriority(Integer.parseInt(line[4]));
                }
                else{
                    task.setPriority(Integer.parseInt(line[3]));
                }
            }
            tasks.computeIfPresent(line[0], (k, v) -> {v.add(task); return v;});
        }

    }
    private boolean isValidDate(String date){
        try{
            LocalDateTime.parse(date);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    public void printTasks(OutputStream outputStream, boolean includePriority, boolean includeCategory) {
        PrintWriter pw = new PrintWriter(outputStream);
        Comparator<Task> priority = Comparator.comparing(Task::isPriority).thenComparing(Task::getPriority)
                .thenComparing(task -> Duration.between(LocalDateTime.now(), task.getDeadline()));
        Comparator<Task> byDefault = Comparator.comparing(task -> Duration.between(LocalDateTime.now(), task.getDeadline()));

        if(includeCategory){
            tasks.forEach((category, tasks) -> {
                pw.println(category.toUpperCase());
                tasks.stream().sorted(includePriority? priority : byDefault).forEach(pw::print);
            });
        } else {
            tasks.values().stream()
                    .flatMap(Collection::stream)
                    .sorted(includePriority? priority:byDefault).forEach(pw::print);
        }


        pw.flush();
    }

}

public class TaskManagerTest {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        System.out.println("Tasks reading");
        manager.readTasks(System.in);
        System.out.println("By categories with priority");
        manager.printTasks(System.out, true, true);
        System.out.println("-------------------------");
        System.out.println("By categories without priority");
        manager.printTasks(System.out, false, true);
        System.out.println("-------------------------");
        System.out.println("All tasks without priority");
        manager.printTasks(System.out, false, false);
        System.out.println("-------------------------");
        System.out.println("All tasks with priority");
        manager.printTasks(System.out, true, false);
        System.out.println("-------------------------");
    }
}