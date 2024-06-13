package vtor_kol;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Word vectors test
 */

class Vector {
    List<Integer> vectors;

    public Vector(List<Integer> vectors) {
        this.vectors = vectors;
    }

    public Vector sum(Vector o){
        return new Vector(IntStream.range(0, o.vectors.size())
                .map(i->vectors.get(i) + o.vectors.get(i))
                .boxed().collect(Collectors.toList()));

    }

    public int max(){
        return vectors.stream().mapToInt(i->i).max().orElse(0);
    }

    @Override
    public String toString() {
        return vectors.toString();
    }
}

class WordVectors{
    List<Vector> vectors;
    Map<String, Vector> wordVector;

    public WordVectors(String[] words, List<List<Integer>> vectors){
        this.wordVector= new TreeMap<>();
        IntStream.range(0, words.length).forEach(i->wordVector.put(words[i], new Vector(vectors.get(i))));
    }

    public void readWords(List<String> words) {
        vectors = words.stream()
                .map(word->wordVector.getOrDefault(word, new Vector(Arrays.asList(5, 5, 5, 5, 5))))
                .collect(Collectors.toList());
    }

    public List<Integer> slidingWindow(int n) {
        return IntStream.range(0, vectors.size()-n+1)
                .mapToObj(i->process(i, n))
                .map(Vector::max)
                .collect(Collectors.toList());
    }
    private Vector process(int ind, int n){
        return vectors.subList(ind, ind+n).stream().reduce(new Vector(Arrays.asList(0, 0, 0, 0, 0)), Vector::sum);
    }
}

public class WordVectorsTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] words = new String[n];
        List<List<Integer>> vectors = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");
            words[i] = parts[0];
            List<Integer> vector = Arrays.stream(parts[1].split(":"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            vectors.add(vector);
        }
        n = scanner.nextInt();
        scanner.nextLine();
        List<String> wordsList = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            wordsList.add(scanner.nextLine());
        }
        WordVectors wordVectors = new WordVectors(words, vectors);
        wordVectors.readWords(wordsList);
        n = scanner.nextInt();
        List<Integer> result = wordVectors.slidingWindow(n);
        System.out.println(result.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",")));
        scanner.close();
    }
}



