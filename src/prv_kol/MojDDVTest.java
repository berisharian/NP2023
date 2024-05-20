package prv_kol;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class AmountNotAllowedException extends Exception{
    public AmountNotAllowedException(int amount) {
        super(String.format("Receipt with amount %d is not allowed to be scanned", amount));
    }
}

enum TaxType{
    A, B, V
}

class Item{
    private int price;
    private TaxType tax;

    public Item(int price, TaxType taxType) {
        this.price = price;
        this.tax = taxType;
    }

    public Item(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public TaxType getTax() {
        return tax;
    }

    public void setTax(TaxType tax) {
        this.tax = tax;
    }
    public double getCalculatedTax(){
        if(tax.equals(TaxType.A)) return 0.18 * this.price;
        else if(tax.equals(TaxType.B)) return 0.05 * this.price;
        else return 0;
    }
}

class Receipt{
    private Long id;
    private List<Item> items;

    public Receipt(Long id, List<Item> items) {
        this.id = id;
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public Receipt(Long id) {
        this.id = id;
        this.items=new ArrayList<>();
    }
    public static Receipt create(String line) throws AmountNotAllowedException {
        String parts[]=line.split("\\s+");
        long id=Long.parseLong(parts[0]);
        List<Item> items=new ArrayList<>();

        Arrays.stream(parts).skip(1).forEach(i-> {
            if(Character.isDigit(i.charAt(0)))
                items.add(new Item(Integer.parseInt(i)));
            else
                items.get(items.size()-1).setTax(TaxType.valueOf(i));
        });
        if(totalAmount(items)>30000)
            throw new AmountNotAllowedException(totalAmount(items));
        return new Receipt(id, items);
    }
    public static int totalAmount(List<Item> items){
        return items.stream().mapToInt(Item::getPrice).sum();
    }
    public int totalAmount(){
        return items.stream().mapToInt(Item::getPrice).sum();
    }
    public double taxReturns(){
        return items.stream().mapToDouble(Item::getCalculatedTax).sum()*0.15;
    }

    @Override
    public String toString() {
        return String.format("%10d\t%10d\t%10.5f", id, totalAmount(), taxReturns()); // id +" " + totalAmount()  + " "+ taxReturns();
    }
}


class MojDDV{
    private List<Receipt> receipts;

    public MojDDV() {
        this.receipts=new ArrayList<>();
    }

    public void readRecords(InputStream in) {
        receipts=new BufferedReader(new InputStreamReader(in))
                .lines()
                .map(line->{
                    try {
                        return Receipt.create(line);
                    }
                    catch (AmountNotAllowedException e){
                        System.out.println(e.getMessage());
                        return null;
                    }
                }).collect(Collectors.toList());
        receipts=receipts.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }


    public void printTaxReturns(PrintStream out) {
        PrintWriter printWriter=new PrintWriter(out);

        receipts.stream().forEach(i->out.println(i));
        printWriter.flush();
    }

    public void printStatistics(PrintStream out) {
        PrintWriter printWriter=new PrintWriter(out);

        DoubleSummaryStatistics summaryStatistics=receipts.stream()
                .mapToDouble(Receipt::taxReturns).summaryStatistics();

        printWriter.println(String.format("min:\t%.3f\nmax:\t%.3f\nsum:\t%.3f\ncount:\t%d\navg:\t%.3f",
                summaryStatistics.getMin(),
                summaryStatistics.getMax(),
                summaryStatistics.getSum(),
                summaryStatistics.getCount(),
                summaryStatistics.getAverage()));

        printWriter.flush();
    }
}

public class MojDDVTest {

    public static void main(String[] args) {

        MojDDV mojDDV = new MojDDV();

        System.out.println("===READING RECORDS FROM INPUT STREAM===");
        mojDDV.readRecords(System.in);

        System.out.println("===PRINTING TAX RETURNS RECORDS TO OUTPUT STREAM ===");
        mojDDV.printTaxReturns(System.out);

        System.out.println("===PRINTING SUMMARY STATISTICS FOR TAX RETURNS TO OUTPUT STREAM===");
        mojDDV.printStatistics(System.out);

    }
}
