package prv_kol;

import java.util.Scanner;

class MinMax<T extends Comparable<T>>{
    private T min;
    private T max;
    private int distinctCount;

    public MinMax() {
    }

    public MinMax(T min, T max, int distinctCount) {
        this.min = min;
        this.max = max;
        this.distinctCount = distinctCount;
    }

    public void update(T element) {
        boolean isDistinct = false;

        if (min == null || element.compareTo(min) < 0) {
            if (min != null && !element.equals(min)) {
                isDistinct = true;
            }
            min = element;
        }

        if (max == null || element.compareTo(max) > 0) {
            if (max != null && !element.equals(max)) {
                isDistinct = true;
            }
            max = element;
        }

        if (min != null && max != null && element.compareTo(min) > 0 && element.compareTo(max) < 0) {
            isDistinct = true;
        }

        if (isDistinct) {
            distinctCount++;
        }
    }

    public T min(){
        return min;
    }
    public T max(){
        return max;
    }

    @Override
    public String toString() {
        return String.format("%s %s %d\n", min, max, distinctCount-1);
    }
}

public class MinAndMax {
    public static void main(String[] args) throws ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        MinMax<String> strings = new MinMax<String>();
        for(int i = 0; i < n; ++i) {
            String s = scanner.next();
            strings.update(s);
        }
        System.out.println(strings);
        MinMax<Integer> ints = new MinMax<Integer>();
        for(int i = 0; i < n; ++i) {
            int x = scanner.nextInt();
            ints.update(x);
        }
        System.out.println(ints);
    }
}