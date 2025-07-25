package hotel_management_system;

public class Customer {
    private final String id, firstName, lastName, phoneNumber, checkIn, checkOut;
    private final double price;

    public Customer(String id, String firstName, String lastName, String phoneNumber, double price, String checkIn, String checkOut) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.price = price;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhoneNumber() { return phoneNumber; }
    public double getPrice() { return price; }
    public String getCheckIn() { return checkIn; }
    public String getCheckOut() { return checkOut; }
}

