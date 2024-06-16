package vtor_kol;


import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

class InvalidIDException extends Exception{
    public InvalidIDException(String id) {
        super(String.format("Invalid %s", id));
    }
}

class InvalidDimensionException extends Exception{
    public InvalidDimensionException() {
        super("Dimension 0 is not allowed!");
    }
}

interface IShape{
    double getPerimeter();
    double getArea();
    void scale(double coef);
}

class Circle implements IShape{
    double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double getPerimeter() {
        return 2*radius * Math.PI;
    }

    @Override
    public double getArea() {
        return radius*radius * Math.PI;
    }

    @Override
    public void scale(double coef) {
        this.radius *= coef;
    }

    @Override
    public String toString() {
        return String.format("Circle -> Radius: %.2f Area: %.2f Perimeter: %.2f", radius, getArea(), getPerimeter());
    }
}

class Rectangle implements IShape{
    double a, b;

    public Rectangle(double a, double b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public double getPerimeter() {
        return 2*a + 2*b;
    }

    @Override
    public double getArea() {
        return a*b;
    }

    @Override
    public void scale(double coef) {
        a*=coef;
        b*=coef;
    }

    @Override
    public String toString() {
        return String.format("Rectangle: -> Sides: %.2f, %.2f Area: %.2f Perimeter: %.2f", a, b, getArea(), getPerimeter());
    }
}
class Square implements IShape{
    double a;

    public Square(double a) {
        this.a = a;
    }

    @Override
    public double getPerimeter() {
        return 4*a;
    }

    @Override
    public double getArea() {
        return a*a;
    }

    @Override
    public void scale(double coef) {
        a*=coef;
    }

    @Override
    public String toString() {
        return String.format("Square: -> Side: %.2f Area: %.2f Perimeter: %.2f", a, getArea(), getPerimeter());
    }
}

class ShapeFactory {
    String id;
    List<IShape> shapes;

    public ShapeFactory(String id, List<IShape> shapes) {
        this.id = id;
        this.shapes = shapes;
    }

    public ShapeFactory(String id) {
        this.id = id;
        this.shapes=new ArrayList<>();
    }
    public void scale(double coef){
        shapes.forEach(x->x.scale(coef));
    }

    public int shapesCount(){
        return shapes.size();
    }

    public double sumOfAreas(){
        return shapes.stream().mapToDouble(IShape::getArea).sum();
    }

    public ShapeFactory create(String line) throws InvalidIDException {
        String [] parts = line.split(" ");
        List<IShape> shape = new ArrayList<>();
        int type = Integer.parseInt(parts[0]);
        String id = parts[1];

        if (isValid(id)){
                throw new InvalidIDException(id);
        }

        double a = Double.parseDouble(parts[2]);
        if (a == 0 ) try {
            throw new InvalidDimensionException();
        } catch (InvalidDimensionException e) {
            System.out.println(e.getMessage());
        }
        switch (type) {
            case 1:
                Circle c = new Circle(a);
                shape.add(c);
                break;
            case 2:
                Square sq = new Square(a);
                shape.add(sq);
                break;
            default:
                double b = Double.parseDouble(parts[3]);
                if(b == 0) try {
                    throw new InvalidDimensionException();
                } catch (InvalidDimensionException e) {
                    System.out.println(e.getMessage());
                }
                Rectangle rec = new Rectangle(a, b);
                shape.add(rec);
        }
        return new ShapeFactory(id, shape);
    }
    boolean isValid(String id){
        if(id.length() != 6)
            return true;
        for(int i =0 ; i<id.length(); i++){
            if (!Character.isLetterOrDigit(id.charAt(i))){
                return true;
            }
        }
        return false;

    }

    @Override
    public String toString() {
        return shapes.stream()
                .sorted(Comparator.comparing(IShape::getPerimeter))
                .map(String::valueOf)
                .collect(Collectors.joining("\n"));
    }
}

class Canvas{
    Map<String, ShapeFactory> shapeFactoryMap;
    Set<IShape> shapes;

    public Canvas() {
        this.shapeFactoryMap= new HashMap<>();
        this.shapes=new TreeSet<>(Comparator.comparing(IShape::getArea));
    }

    public void readShapes(InputStream in)  {
        BufferedReader br= new BufferedReader(new InputStreamReader(in));
        ShapeFactory shapeFactory = new ShapeFactory("");

        br.lines().forEach(line -> {


            try {
                ShapeFactory factory = shapeFactory.create(line);

                factory.shapes.forEach(shape -> {
                    shapes.add(shape);
                    shapeFactoryMap.computeIfAbsent(factory.id, k -> new ShapeFactory(factory.id)).shapes.add(shape);
                });
            } catch (InvalidIDException e) {
                System.out.println(e.getMessage());
            }


        });
    }

    public void printAllShapes(PrintStream out) {
        PrintWriter pw = new PrintWriter(out);
        shapes.forEach(pw::println);

        pw.flush();
    }

    public void scaleShapes(String userId, double coef) {
        if(!shapeFactoryMap.containsKey(userId))
           return;
        shapeFactoryMap.get(userId).scale(coef);
    }

    public void printByUserId(PrintStream out) {
        PrintWriter pw = new PrintWriter(out);

        shapeFactoryMap.values().stream()
                .sorted(Comparator.comparing(ShapeFactory::shapesCount).reversed()
                        .thenComparing(ShapeFactory::sumOfAreas))
                        .forEach(x ->{
                            pw.println("Shapes of user: " + x.id);
                            pw.println(x);
                        });

        pw.flush();
    }

    public void statistics(PrintStream out) {
        PrintWriter pw= new PrintWriter(out);

        DoubleSummaryStatistics ds = shapes.stream().mapToDouble(IShape::getArea).summaryStatistics();
        pw.println(String.format("count: %d\nsum: %.2f\nmin: %.2f\naverage: %.2f\nmax: %.2f",
                ds.getCount(), ds.getSum(), ds.getMin(), ds.getAverage(), ds.getMax()));

        pw.flush();
    }
}

public class CanvasTest {

    public static void main(String[] args) {
        Canvas canvas = new Canvas();

        System.out.println("READ SHAPES AND EXCEPTIONS TESTING");
        canvas.readShapes(System.in);
        System.out.println("BEFORE SCALING");
        canvas.printAllShapes(System.out);
        canvas.scaleShapes("123456", 1.5);
        System.out.println("AFTER SCALING");
        canvas.printAllShapes(System.out);

        System.out.println("PRINT BY USER ID TESTING");
        canvas.printByUserId(System.out);

        System.out.println("PRINT STATISTICS");
        canvas.statistics(System.out);
    }
}
