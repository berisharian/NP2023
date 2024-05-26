package prv_kol.FrontPageTest;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

class CategoryNotFoundException extends Exception{
    public CategoryNotFoundException(String message) {
        super(String.format("Category %s was not found", message));
    }
}
class NewsItem{
    protected String title;
    protected Date date;
    protected Category category;

    public NewsItem(String title, Date date, Category category) {
        this.title = title;
        this.date = date;
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }
    public String getTeaser() {
        return "Coming soon!";
    }
}
class Category{
    private String name;

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class TextNewsItem extends NewsItem{
    private String text;
    public TextNewsItem(String title, Date date, Category category, String text) {
        super(title, date, category);
        this.text= text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String getTeaser() {
        long duration= Calendar.getInstance().getTimeInMillis() - date.getTime();
        return String.format("%s\n%d\n%s", title, TimeUnit.MILLISECONDS.toMinutes(duration),
                text.length() < 80 ? text : text.substring(0, 80));
    }
}

class MediaNewsItem extends NewsItem{
    private String url;
    private int views;

    public MediaNewsItem(String title, Date date, Category category, String url, int views) {
        super(title, date, category);
        this.url = url;
        this.views = views;
    }

    @Override
    public String getTeaser() {
        long duration= Calendar.getInstance().getTimeInMillis() - date.getTime();
        return String.format("%s\n%d\n%s\n%d", title, TimeUnit.MILLISECONDS.toMinutes(duration), url, views);
    }
}
class FrontPage{
    List<NewsItem> newsItems;
    Category [] categories;

    public FrontPage(Category[] categories) {
        this.categories = categories;
        this.newsItems=new ArrayList<>();
    }
    public void addNewsItem(NewsItem newsItem){
        newsItems.add(newsItem);
    }
    public List<NewsItem> listByCategory(Category category){
        return newsItems.stream().filter(i->i.getCategory().equals(category)).collect(Collectors.toList());
    }
    public List<NewsItem> listByCategoryName(String category) throws CategoryNotFoundException {
        for(Category c: categories){
            if(c.getName().equals(category))
                return newsItems.stream().filter(cat->cat.getCategory().getName().equals(category)).collect(Collectors.toList());
        }
        throw new CategoryNotFoundException(category);
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        newsItems.forEach(x-> sb.append(x.getTeaser()).append("\n"));
        return sb.toString();
    }
}

public class FrontPageTest {
    public static void main(String[] args) {
        // Reading
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] parts = line.split(" ");
        Category[] categories = new Category[parts.length];
        for (int i = 0; i < categories.length; ++i) {
            categories[i] = new Category(parts[i]);
        }
        int n = scanner.nextInt();
        scanner.nextLine();
        FrontPage frontPage = new FrontPage(categories);
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            cal = Calendar.getInstance();
            int min = scanner.nextInt();
            cal.add(Calendar.MINUTE, -min);
            Date date = cal.getTime();
            scanner.nextLine();
            String text = scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            TextNewsItem tni = new TextNewsItem(title, date, categories[categoryIndex], text);
            frontPage.addNewsItem(tni);
        }

        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int min = scanner.nextInt();
            cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -min);
            scanner.nextLine();
            Date date = cal.getTime();
            String url = scanner.nextLine();
            int views = scanner.nextInt();
            scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            MediaNewsItem mni = new MediaNewsItem(title, date, categories[categoryIndex], url, views);
            frontPage.addNewsItem(mni);
        }
        // Execution
        String category = scanner.nextLine();
        System.out.println(frontPage);
        for(Category c : categories) {
            System.out.println(frontPage.listByCategory(c).size());
        }
        try {
            System.out.println(frontPage.listByCategoryName(category).size());
        } catch(CategoryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}


// Vasiot kod ovde