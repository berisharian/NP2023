package prv_kol;


import com.sun.jdi.DoubleValue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
class Triple<T extends Number>{
    private T first;
    private T second;
    private T third;

    public Triple(T first, T second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public double max() {
        List<T> list=new ArrayList<>(List.of(first, second, third));
    //    list.sort((a, b) -> Double.compare(a.doubleValue(), b.doubleValue()));
        //    return list.get(2).doubleValue();
        return   list.stream().mapToDouble(Number::doubleValue).max().getAsDouble();


    }

    public double avarage() {
        return (first.doubleValue() + second.doubleValue() + third.doubleValue())/3;
    }

    public void sort() {
        List<T> list=new ArrayList<>(List.of(first, second, third));
        list.sort((a, b) -> Double.compare(a.doubleValue(), b.doubleValue()));
        first = list.get(0);
        second = list.get(1);
        third = list.get(2);
    }

    @Override
    public String toString() {
        return String.format("%.2f %.2f %.2f", first.doubleValue(), second.doubleValue(), third.doubleValue());
    }
}

public class TripleTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int a = scanner.nextInt();
        int b = scanner.nextInt();
        int c = scanner.nextInt();
        Triple<Integer> tInt = new Triple<Integer>(a, b, c);
        System.out.printf("%.2f\n", tInt.max());
        System.out.printf("%.2f\n", tInt.avarage());
        tInt.sort();
        System.out.println(tInt);
        float fa = scanner.nextFloat();
        float fb = scanner.nextFloat();
        float fc = scanner.nextFloat();
        Triple<Float> tFloat = new Triple<Float>(fa, fb, fc);
        System.out.printf("%.2f\n", tFloat.max());
        System.out.printf("%.2f\n", tFloat.avarage());
        tFloat.sort();
        System.out.println(tFloat);
        double da = scanner.nextDouble();
        double db = scanner.nextDouble();
        double dc = scanner.nextDouble();
        Triple<Double> tDouble = new Triple<Double>(da, db, dc);
        System.out.printf("%.2f\n", tDouble.max());
        System.out.printf("%.2f\n", tDouble.avarage());
        tDouble.sort();
        System.out.println(tDouble);
    }
}
// vasiot kod ovde
// class Triple


