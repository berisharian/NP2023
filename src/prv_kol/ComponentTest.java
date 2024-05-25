package prv_kol;

import java.util.*;

class InvalidPositionException extends Exception{
    public InvalidPositionException(int pos) {
        super(String.format("Invalid position %d, alredy taken!", pos));
    }
}

class Component implements Comparable<Component>{
    private String color;
    private int weight;
    List<Component> components;

    public Component(String color, int weight) {
        this.color = color;
        this.weight = weight;
        components=new ArrayList<>();
    }
    public void addComponent(Component component){
        components.add(component);
        components.sort(Comparator.naturalOrder());
    }

    @Override
    public int compareTo(Component o) {
        if(this.weight != o.weight)
            return Integer.compare(this.weight, o.weight);
        return this.color.compareTo(o.color);
    }
    public void changeColor(int weight, String color){
        if(this.weight < weight)
            this.color=color;
        for(Component c: components){
            c.changeColor(weight, color);
        }
    }

    public String getColor() {
        return color;
    }

    public int getWeight() {
        return weight;
    }

    public String format(String lines){
        StringBuilder sb=new StringBuilder();
        sb.append(String.format("%s%d:%s\n", lines, weight, color));
        components.forEach(x-> sb.append(x.format(lines+ "---")));
        return sb.toString();
    }

    @Override
    public String toString() {
       StringBuilder sb=new StringBuilder();
       sb.append(String.format("%d:%s\n", weight, color));
       components.forEach(x-> sb.append(x.format("---")));
       return sb.toString();
    }
}

class Window{
    private String name;
    Map<Integer, Component> components;

    public Window(String name) {
        this.name = name;
        this.components=new TreeMap<>();
    }
    void addComponent(int position, Component component) throws InvalidPositionException {
        if (components.containsKey(position)) {
            throw new InvalidPositionException(position);
        }
        components.put(position, component);
    }

    public void changeColor(int weight, String color){
        components.values().forEach(c->c.changeColor(weight, color));
    }
    public void swichComponents(int pos1, int pos2){
        Component pos3= components.get(pos1);
        components.put(pos1, components.get(pos2));
        components.put(pos2, pos3);
    }


    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append(String.format("WINDOW %s\n", name));
        int indx=0;
        for(Component c: components.values()){
            sb.append(indx+1).append(":").append(c);
            indx++;
        }
        return sb.toString();
    }
}

public class ComponentTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        Window window = new Window(name);
        Component prev = null;
        while (true) {
            try {
                int what = scanner.nextInt();
                scanner.nextLine();
                if (what == 0) {
                    int position = scanner.nextInt();
                    window.addComponent(position, prev);
                } else if (what == 1) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev = component;
                } else if (what == 2) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                    prev = component;
                } else if (what == 3) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                } else if(what == 4) {
                    break;
                }

            } catch (InvalidPositionException e) {
                System.out.println(e.getMessage());
            }
            scanner.nextLine();
        }

        System.out.println("=== ORIGINAL WINDOW ===");
        System.out.println(window);
        int weight = scanner.nextInt();
        scanner.nextLine();
        String color = scanner.nextLine();
        window.changeColor(weight, color);
        System.out.println(String.format("=== CHANGED COLOR (%d, %s) ===", weight, color));
        System.out.println(window);
        int pos1 = scanner.nextInt();
        int pos2 = scanner.nextInt();
        System.out.println(String.format("=== SWITCHED COMPONENTS %d <-> %d ===", pos1, pos2));
        window.swichComponents(pos1, pos2);
        System.out.println(window);
    }
}

// вашиот код овде
