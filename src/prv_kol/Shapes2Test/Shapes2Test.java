package prv_kol.Shapes2Test;


import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class IrregularCanvasException extends Exception{
    public IrregularCanvasException(String id, double maxArea) {
        super(String.format("Canvas %s has a shape with area larger than %.2f", id, maxArea));
    }
}

enum ShapeType{
    C, S
}
class Shape implements Comparable<Shape>{

    private ShapeType type;
    private int size;

    public void setType(ShapeType type) {
        this.type = type;
    }

    public Shape(ShapeType type, int size) {
        this.type = type;
        this.size = size;
    }

    public Shape(ShapeType type) {
        this.type = type;
    }

    public ShapeType getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Shape(int size) {
        this.size = size;
    }
    public double calculatedArea(){
        if(type.equals(ShapeType.C)) return  (Math.PI * Math.pow(size, 2));
        else if(type==ShapeType.S) return size*size;
        else return 0;
    }

    @Override
    public int compareTo(Shape o) {
        return Double.compare(o.calculatedArea(), this.calculatedArea());
    }
}

class ShapeFactory implements Comparable<ShapeFactory>{
    private String id;
    private List<Shape> shapes;

    public String getId() {
        return id;
    }

    public ShapeFactory(String id, List<Shape> shapes) {
        this.id = id;
        this.shapes = shapes;
    }

    //0cc31e47 C 27 C 13 C 29 C 15 C 22
    public static ShapeFactory create(String line, double maxArea) throws IrregularCanvasException {
        String [] parts=line.split("\\s+");
        String id=parts[0];

        List<Shape> shapes=new ArrayList<>();
        Arrays.stream(parts).skip(1)
                .forEach(i -> {
                    if(Character.isAlphabetic(i.charAt(0))){
                        shapes.add(new Shape(ShapeType.valueOf(i)));
                    }
                    else {
                        shapes.get(shapes.size()-1).setSize(Integer.parseInt(i));
                    }
                });
    //    System.out.println(shapes);
        if(totalArea(shapes) > maxArea){
            throw new IrregularCanvasException(id, maxArea);
        }
        return new ShapeFactory(id, shapes);
    }
    public static double totalArea(List<Shape> shapes){
        return shapes.stream().mapToDouble(Shape::calculatedArea).sum();
    }

    public int getCircleCount(){
        return (int) shapes.stream().filter(shape -> shape.getType().equals(ShapeType.C)).count();
    }

    public int getSquareCount(){
        return (int) shapes.stream().filter(shape -> shape.getType().equals(ShapeType.S)).count();
    }

    public double sumOfAreas(){
        return shapes.stream().mapToDouble(Shape::calculatedArea).sum();
    }

    @Override
    public int compareTo(ShapeFactory o) {
        return Double.compare(o.sumOfAreas(), this.sumOfAreas());
    }

    @Override
    public String toString() {
        DoubleSummaryStatistics summaryStatistics=shapes.stream().mapToDouble(Shape::calculatedArea).summaryStatistics();
        return String.format("%s %d %d %d %.2f %.2f %.2f", id, shapes.size(), getCircleCount(), getSquareCount(),
                summaryStatistics.getMin(), summaryStatistics.getMax(), summaryStatistics.getAverage());
    }


}

class ShapesApplication{
    private List<ShapeFactory> shapeFactories;
    private float maxArea;

    public ShapesApplication(int maxArea) {
        this.maxArea = maxArea;
        this.shapeFactories=new ArrayList<>();
    }

    public void readCanvases(InputStream in) {
        BufferedReader br=new BufferedReader(new InputStreamReader(in));
        shapeFactories=br.lines()
                .map(line-> {
                    try {
                        return ShapeFactory.create(line, maxArea);
                    } catch (IrregularCanvasException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                }).collect(Collectors.toList());
        shapeFactories=shapeFactories.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public void printCanvases(PrintStream out) {
        PrintWriter printWriter=new PrintWriter(out);

        shapeFactories.stream().sorted().forEach(out::println);

        printWriter.flush();
    }
}

public class Shapes2Test {

    public static void main(String[] args) {

        ShapesApplication shapesApplication = new ShapesApplication(10000);

        System.out.println("===READING CANVASES AND SHAPES FROM INPUT STREAM===");
        shapesApplication.readCanvases(System.in);

        System.out.println("===PRINTING SORTED CANVASES TO OUTPUT STREAM===");
        shapesApplication.printCanvases(System.out);


    }
}
