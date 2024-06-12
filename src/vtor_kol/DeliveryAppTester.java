package vtor_kol;

import java.util.*;

/*
YOUR CODE HERE
DO NOT MODIFY THE interfaces and classes below!!!
*/



interface Location {
    int getX();

    int getY();

    default int distance(Location other) {
        int xDiff = Math.abs(getX() - other.getX());
        int yDiff = Math.abs(getY() - other.getY());
        return xDiff + yDiff;
    }
}

class LocationCreator {
    public static Location create(int x, int y) {

        return new Location() {
            @Override
            public int getX() {
                return x;
            }

            @Override
            public int getY() {
                return y;
            }
        };
    }
}
class DeliveryPerson{
    String id;
    String name;
    Location location;
    int totalDeliveries;
    List<Integer> moneyEarned;

    public DeliveryPerson(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.totalDeliveries =0;
        this.moneyEarned=new ArrayList<>();
    }

    public int compareDistanceToRestaurant(DeliveryPerson other, Location restaurantLocation){
        int thisDistance=location.distance(restaurantLocation);
        int otherDistance=other.location.distance(restaurantLocation);
        if(thisDistance == otherDistance){
            return Integer.compare(this.totalDeliveries, other.totalDeliveries);
        }
        return thisDistance-otherDistance;
    }

    public void processDelivery(int distance, Location location){
        this.location=location;
        this.totalDeliveries++;
        this.moneyEarned.add(90 + 10*(distance/10));
    }

    public int getTotalDeliveries(){
        return totalDeliveries;
    }
    public double totalEarned(){
        return moneyEarned.stream().mapToDouble(i->i).sum();
    }

    public String getId() {
        return id;
    }

    @Override
    //ID: 2 Name: Riste Total deliveries: 1 Total delivery fee: 90.00 Average delivery fee: 90.00
    public String toString() {
        DoubleSummaryStatistics ds =moneyEarned.stream().mapToDouble(i->i).summaryStatistics();
        return String.format(
                "ID: %s Name: %s Total deliveries: %d Total delivery fee: %.2f Average delivery fee: %.2f",
                id, name, totalDeliveries, ds.getSum(), ds.getAverage());

    }
}

class Restaurant{
    String id;
    String name;
    Location location;
    List<Float> moneyEarned;

    public Restaurant(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.moneyEarned= new ArrayList<>();
    }

    public void processDelivery(float cost) {
        moneyEarned.add(cost);
    }

    public double averageIncome(){
       return moneyEarned.stream().mapToDouble(i->i).average().orElse(0.0);
    }

    public String getId() {
        return id;
    }

    @Override

    public String toString() {
       DoubleSummaryStatistics ds= moneyEarned.stream().mapToDouble(i->i).summaryStatistics();
       return String.format(
               "ID: %s Name: %s Total orders: %d Total amount earned: %.2f Average amount earned: %.2f",
                id, name, ds.getCount(), ds.getSum(), ds.getAverage());
    }
}

class Address{
    String name;
    Location location;

    public Address(String name, Location location) {
        this.name = name;
        this.location = location;
    }
}

class User{
    String id;
    String name;

    Map<String, Address> addresses;
    List<Float> moneySpent;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
        this.addresses=new HashMap<>();
        this.moneySpent=new ArrayList<>();
    }

    public void processDelivery(float cost) {
        moneySpent.add(cost);
    }
    public double totalSpent(){
       return moneySpent.stream().mapToDouble(i->i).sum();
    }

    @Override
    //ID: 1 Name: stefan Total orders: 1 Total amount spent: 450.00 Average amount spent: 450.00
    public String toString() {
        DoubleSummaryStatistics ds= moneySpent.stream().mapToDouble(i->i).summaryStatistics();
        return String.format
                ("ID: %s Name: %s Total orders: %d Total amount spent: %.2f Average amount spent: %.2f",
                id, name, ds.getCount(), ds.getSum(), ds.getAverage());
    }

    public String getId() {
        return id;
    }
}

class DeliveryApp{
    String name;
    Map<String, User> users;
    Map<String, DeliveryPerson> deliveryPersons;
    Map<String, Restaurant> restaurants;

    public DeliveryApp(String name) {
        this.name = name;
        this.users=new HashMap<>();
        this.deliveryPersons=new HashMap<>();
        this.restaurants=new HashMap<>();
    }

    public void registerDeliveryPerson(String id, String name, Location currentLocation) {
        deliveryPersons.put(id, new DeliveryPerson(id, name, currentLocation));
    }

    public void addRestaurant(String id, String name, Location location) {
        restaurants.put(id, new Restaurant(id, name, location));
    }

    public void addUser(String id, String name) {
        users.put(id, new User(id, name));
    }

    public void addAddress(String id, String addressName, Location location) {
        users.get(id).addresses.put(addressName, new Address(addressName, location));
    }

    public void orderFood(String userId, String userAddressName, String restaurantId, float cost) {
        User user= users.get(userId);
        Restaurant restaurant=restaurants.get(restaurantId);
        Address address=user.addresses.get(userAddressName);

        DeliveryPerson deliveryPerson=deliveryPersons.values()
                .stream().min((l, r) -> l.compareDistanceToRestaurant(r, restaurant.location)).get();

        int distance = deliveryPerson.location.distance(restaurant.location);
        deliveryPerson.processDelivery(distance, address.location);
        user.processDelivery(cost);
        restaurant.processDelivery(cost);
    }

    void printUsers() {
        users.values().stream()
                .sorted(Comparator.comparing(User::totalSpent).thenComparing(User::getId).reversed())
                .forEach(System.out::println);
    }

    void printRestaurants() {
        restaurants.values().stream()
                .sorted(Comparator.comparing(Restaurant::averageIncome).thenComparing(Restaurant::getId).reversed())
                .forEach(System.out::println);
    }

    void printDeliveryPeople() {
        deliveryPersons.values().stream()
                .sorted(Comparator.comparing(DeliveryPerson::totalEarned).thenComparing(DeliveryPerson::getId).reversed())
                .forEach(System.out::println);
    }
}

public class DeliveryAppTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String appName = sc.nextLine();
        DeliveryApp app = new DeliveryApp(appName);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(" ");

            if (parts[0].equals("addUser")) {
                String id = parts[1];
                String name = parts[2];
                app.addUser(id, name);
            } else if (parts[0].equals("registerDeliveryPerson")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.registerDeliveryPerson(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addRestaurant")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addRestaurant(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addAddress")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addAddress(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("orderFood")) {
                String userId = parts[1];
                String userAddressName = parts[2];
                String restaurantId = parts[3];
                float cost = Float.parseFloat(parts[4]);
                app.orderFood(userId, userAddressName, restaurantId, cost);
            } else if (parts[0].equals("printUsers")) {
                app.printUsers();
            } else if (parts[0].equals("printRestaurants")) {
                app.printRestaurants();
            } else {
                app.printDeliveryPeople();
            }

        }
    }
}
