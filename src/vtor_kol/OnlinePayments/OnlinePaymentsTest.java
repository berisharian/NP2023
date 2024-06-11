package vtor_kol.OnlinePayments;

import java.io.*;
import java.sql.Struct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Student{
    String id;
    List<Item> items;

    public Student(String id) {
        this.id = id;
        this.items=new ArrayList<>();
    }

    public String getId() {
        return id;
    }
    public void addItem(Item item){
        items.add(item);
    }
    public int neto(){
        return items.stream().mapToInt(i->i.price).sum();
    }
    public int fee(){
        int total= neto();
        double fee = Math.round(total*0.0114);
        if(fee>300)
            fee=300;
        if(fee<3)
            return 3;
        return (int) fee;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append(String.format("Student: %s Net: %d Fee: %d Total: %d\n", id, neto(), fee(),  neto()+fee()));
        sb.append("Items:\n");
        items.sort(Comparator.comparing(Item::getPrice).reversed());

        List<String> outputs = new ArrayList<>();
        for (int i=0; i<items.size(); i++){
            outputs.add(String.format("%d. %s", i+1, items.get(i).toString()));
        }
        sb.append(outputs.stream().collect(Collectors.joining("\n")));
        return sb.toString();
    }
}

class Item {
    String id;
    String name;
    int price;

    public Item(String id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
    public static Item create(String line){
        String [] parts=line.split(";");
        return new Item(parts[0], parts[1], Integer.parseInt(parts[2]));
    }

//    @Override
//    public int compareTo(Item o) {
//        return Integer.compare(o.price, this.price);
//    }

    @Override
    public String toString() {
        return String.format("%s %d", name, price);
    }

    public int getPrice() {
        return price;
    }
}

class OnlinePayments{
    Map<String, Student> payments;

    public OnlinePayments() {
        this.payments = new HashMap<>();
    }

    public void readItems(InputStream in) {
        BufferedReader br=new BufferedReader(new InputStreamReader(in));
        br.lines()
                .map(Item::create)
                .forEach(item -> {
                    payments.putIfAbsent(item.id, new Student(item.id));
                    payments.get(item.id).addItem(item);
                });
    }

    public void printStudentReport(String id, PrintStream out) {
        PrintWriter pw = new PrintWriter(out);
        Student s= payments.get(id);
        if (s == null)
            pw.println(String.format("Student %s not found!", id));
        else
            pw.println(s);

        pw.flush();
    }
}

public class OnlinePaymentsTest {
    public static void main(String[] args) {
        OnlinePayments onlinePayments = new OnlinePayments();

        onlinePayments.readItems(System.in);

        IntStream.range(151020, 151025).mapToObj(String::valueOf).forEach(id -> onlinePayments.printStudentReport(id, System.out));
    }
}
