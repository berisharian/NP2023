package vtor_kol;

import java.util.*;
import java.util.stream.Collectors;

class Movie implements Comparable<Movie>{
    String title;
    List<Integer> ratings;

    public Movie(String title, int[] ratings) {
        this.title = title;
        this.ratings=new ArrayList<>();
        this.ratings.addAll(Arrays.stream(ratings).boxed().collect(Collectors.toList()));
    }

    public String getTitle() {
        return title;
    }

    public List<Integer> getRatings() {
        return ratings;
    }

    public double getAverageRating(){
        return this.ratings.stream().mapToDouble(i->i).average().orElse(0.0);
    }

    @Override
    public int compareTo(Movie o) {
         int res = Double.compare(o.getAverageRating(), this.getAverageRating());
         if (res == 0)
             return this.title.compareTo(o.title);
         return res;
    }
    public double getMax(){
        return ratings.stream().mapToDouble(i->i).max().orElse(1);
    }

    public double getByRatingCoeff(int max){
        return getAverageRating()*ratings.size()/getMax();
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f) of %d ratings",  title, getAverageRating(), ratings.size());
    }
}

class MoviesList{
    List<Movie> movies;

    public MoviesList() {
        this.movies = new ArrayList<>();
    }

    public void addMovie(String title, int[] ratings) {
        movies.add(new Movie(title, ratings));
    }

    public List<Movie> top10ByAvgRating() {
        return movies.stream().sorted().limit(10).collect(Collectors.toList());
    }

    public List<Movie> top10ByRatingCoef() {
        int maxRating = movies.stream().map(x->x.getRatings().size()).reduce(0, Math::max);
//        Comparator<Movie> cmp = (o1, o2) -> {
//            int rating = Double.compare(o2.getByRatingCoeff(maxRating), o1.getByRatingCoeff(maxRating));
//            if (rating==0)
//                return o1.getTitle().compareTo(o2.getTitle());
//            return rating;
//        };

        Comparator<Movie> comp= Comparator.comparing(Movie::getAverageRating).reversed()
                .thenComparing(Movie::getTitle);
        return movies.stream().sorted(comp).limit(10).collect(Collectors.toList());

    }
}

public class MoviesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MoviesList moviesList = new MoviesList();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int x = scanner.nextInt();
            int[] ratings = new int[x];
            for (int j = 0; j < x; ++j) {
                ratings[j] = scanner.nextInt();
            }
            scanner.nextLine();
            moviesList.addMovie(title, ratings);
        }
        scanner.close();
        List<Movie> movies = moviesList.top10ByAvgRating();
        System.out.println("=== TOP 10 BY AVERAGE RATING ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
        movies = moviesList.top10ByRatingCoef();
        System.out.println("=== TOP 10 BY RATING COEFFICIENT ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
    }
}

// vashiot kod ovde
