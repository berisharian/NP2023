package prv_kol.ShoppingTest;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;

class InvalidOperationException extends Exception{
    public InvalidOperationException(String message) {
        super(message);
    }
}

abstract class Product implements Comparable<Product>{
    private int id;
    private String name;
    private int price;

    public Product(int id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    abstract float totalPrice();

    @Override
    public int compareTo(Product o) {
        return Float.compare(o.totalPrice(), this.totalPrice());
    }
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append(String.format("%d %s", id, totalPrice()));
        return sb.toString();
    }
}


class ProductFactory{

    //PS;107965;Flour;409;800.78
    public static Product create(String line) throws InvalidOperationException {
    String []parts=line.split(";");
    String type= parts[0];
    int id=Integer.parseInt(parts[1]);
    String name= parts[2];
    int price=Integer.parseInt(parts[3]);
    String quantity=parts[4];
    if(quantity.equals("0"))
        throw new InvalidOperationException(String.format
                ("The quantity of the product with id %s can not be 0.", id));

    if(type.equals("WS"))
       return new WSProduct(id, name, price, Integer.parseInt(quantity));
    else
        return new PSProduct(id, name, price, Float.parseFloat(quantity));
    }
}
class WSProduct extends Product{
    int quantity;

    public WSProduct(int id, String name, int price, int quanitity) {
        super(id, name, price);
        this.quantity = quanitity;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append(String.format("%d - %.02f", getId(), totalPrice()));
        return sb.toString();
    }
    @Override
    float totalPrice() {
        return  quantity * getPrice();
    }
}
class PSProduct extends Product{
    float quantity;

    public PSProduct(int id, String name, int price, float quantity) {
        super(id, name, price);
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append(String.format("%d - %.02f", getId(), totalPrice()));
        return sb.toString();
    }
    @Override
    float totalPrice() {
        return (float) (quantity/1000.0 * getPrice());
    }
}

class ShoppingCart{
    List<Product> products;

    public ShoppingCart() {
        this.products=new ArrayList<>();
    }

    public void addItem(String nextLine) throws InvalidOperationException {
        products.add(ProductFactory.create(nextLine));
    }

//    public void totalPrice(List<Product> products){
//        float sum=0;
//        products.stream().mapToInt(i-> i.getPrice()).sum()
//    }

    public void printShoppingCart(PrintStream out) {
        PrintWriter pw=new PrintWriter(out);

       products.stream().sorted().forEach(i->pw.println(i));
      //   products.stream().sorted(Comparator.reverseOrder()).forEach(i-> pw.println(i.totalPrice()));
        pw.flush();
    }

    public void blackFridayOffer(List<Integer> discountItems, PrintStream out) throws InvalidOperationException {
        PrintWriter pw=new PrintWriter(out);

        if(discountItems.isEmpty())
            throw new InvalidOperationException(String.format("There are no products with discount."));

        ArrayList<Product> disc=new ArrayList<>();
//        for(int i=0; i<products.size(); i++){
//            if(discountItems.contains(products.get(i).getId())){
//                products.get(i).setPrice((int) (products.get(i).getPrice() * 0.9));
//            }
//        }
//
        for(Product item: products){
            for (Integer d: discountItems){
                    if(item.getId()== d){
                        disc.add(item);
                    }
            }
        }

//        public Double getDiscount() {
//            return price - price * 0.1f;
//        }
        for(Product item: disc){
            double original= item.totalPrice();
            original *= 0.9;
            pw.println(String.format("%s - %.2f", item.getId(),  item.totalPrice() - original));
        }


        pw.flush();
    }
}

public class ShoppingTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ShoppingCart cart = new ShoppingCart();

        int items = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < items; i++) {
            try {
                cart.addItem(sc.nextLine());
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        }

        List<Integer> discountItems = new ArrayList<>();
        int discountItemsCount = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < discountItemsCount; i++) {
            discountItems.add(Integer.parseInt(sc.nextLine()));
        }

        int testCase = Integer.parseInt(sc.nextLine());
        if (testCase == 1) {
            cart.printShoppingCart(System.out);
        } else if (testCase == 2) {
            try {
                cart.blackFridayOffer(discountItems, System.out);
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Invalid test case");
        }
    }
}
