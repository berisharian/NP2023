package prv_kol;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class Shape implements Comparable<Shape>{
    private String canvas_id;
    private List<Integer> sizes;

    public Shape(String canvas_id, List<Integer> sizes) {
        this.canvas_id = canvas_id;
        this.sizes=sizes;
    }

    public int getSizes(){
        return sizes.size();
    }
    public int perimeter(){
        return 4*sizes.stream().mapToInt(s->s).sum();
    }

    @Override

    public int compareTo(Shape o) {
        return Integer.compare(this.perimeter(), o.perimeter());
    }

    @Override
    public String toString() {
        return String.format("%s %d %d",canvas_id, sizes.size(), perimeter()  );
    }
}
class ShapeFactory {
    //364fbe94 24 30 22 33 32 30 37 18 29 27 33 21 27 26
    public static Shape createShape(String line){
        String []parts=line.split("\\s+");
        String id=parts[0];
        List<Integer> s= new ArrayList<>();
//        for(int i=1;i<parts.length;i++){
//            s.add(Integer.parseInt(parts[i]));
//        }
        Arrays.stream(parts).skip(1).map(Integer::parseInt).collect(Collectors.toList());
        return new Shape(id, s);
    }
}

class ShapesApplication{
    List<Shape> shapes;

    public ShapesApplication() {

        this.shapes = new ArrayList<>();
    }
    public int readCanvases (InputStream inputStream){
        BufferedReader bf=new BufferedReader(new InputStreamReader(inputStream));

        shapes=bf.lines()
                .map(line->ShapeFactory.createShape(line)).collect(Collectors.toList());
        return shapes.stream().mapToInt(sh -> sh.getSizes()).sum();
    }
    public void printLargestCanvasTo (OutputStream outputStream){
        PrintWriter printWriter=new PrintWriter(outputStream, true);
        Shape s=shapes.stream().sorted(Comparator.reverseOrder()).findFirst().get();
        printWriter.println(s);
    }

}

public class Shapes1Test {
    public static void main(String[] args) {
        ShapesApplication shapesApplication = new ShapesApplication();

        System.out.println("===READING SQUARES FROM INPUT STREAM===");
        System.out.println(shapesApplication.readCanvases(System.in));
        System.out.println("===PRINTING LARGEST CANVAS TO OUTPUT STREAM===");
        shapesApplication.printLargestCanvasTo(System.out);

    }
}
