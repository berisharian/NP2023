package vtor_kol;

import com.sun.source.tree.Tree;

import java.util.*;

class DuplicateNumberException extends Exception{
    public DuplicateNumberException(String number) {
        super(String.format("Duplicate number: %s", number));
    }
}

class Contact implements Comparable<Contact>{
    String name;
    String number;

    public Contact(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public int compareTo(Contact o) {
        int res = this.name.compareTo(o.name);
        if(res == 0)
            return this.number.compareTo(o.number);
        return res;
    }

    @Override
    public String toString() {
        return String.format("%s %s", name, number);
    }
}

class PhoneBook{
    Set<String> uniqueNumber;
    Map<String, Set<Contact>> contactsBySubnumber;
    Map<String, Set<Contact>> contactsByName;

    public PhoneBook() {
        this.uniqueNumber=new HashSet<>();
        this.contactsBySubnumber=new HashMap<>();
        this.contactsByName=new HashMap<>();
    }
    private List<String> getSubNumbers(String number){
        List<String> result = new ArrayList<>();
        for (int len=3; len <= number.length(); len++){
            for (int i=0; i<=number.length() - len; i++){
                result.add(number.substring(i, i+len));
            }
        }
        return result;
    }

    public void addContact(String name, String number) throws DuplicateNumberException {
        if (uniqueNumber.contains(number))
            throw new DuplicateNumberException(number);

        Contact c=new Contact(name, number);
        uniqueNumber.add(number);

        getSubNumbers(number).forEach(subnumber -> {
            contactsBySubnumber.putIfAbsent(subnumber, new TreeSet<>());
            contactsBySubnumber.get(subnumber).add(c);
        });
        contactsByName.putIfAbsent(name, new TreeSet<>());
        contactsByName.get(name).add(c);
        
    }

    public void contactsByNumber(String number) {
        if(!contactsBySubnumber.containsKey(number)){
            System.out.println("NOT FOUND");
            return;
        }
        contactsBySubnumber.get(number).forEach(System.out::println);

    }

    public void contactsByName(String name) {
        if(!contactsByName.containsKey(name)){
            System.out.println("NOT FOUND");
            return;
        }
        contactsByName.get(name).forEach(System.out::println);
    }
}

public class PhoneBookTest {

    public static void main(String[] args) {
        PhoneBook phoneBook = new PhoneBook();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            try {
                phoneBook.addContact(parts[0], parts[1]);
            } catch (DuplicateNumberException e) {
                System.out.println(e.getMessage());
            }
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(line);
            String[] parts = line.split(":");
            if (parts[0].equals("NUM")) {
                phoneBook.contactsByNumber(parts[1]);
            } else {
                phoneBook.contactsByName(parts[1]);
            }
        }
    }

}

// Вашиот код овде


