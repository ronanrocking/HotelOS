package com.example;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Room {
    private final StringProperty roomNumber = new SimpleStringProperty();
    private final StringProperty roomType   = new SimpleStringProperty();
    private final DoubleProperty price      = new SimpleDoubleProperty();
    private final BooleanProperty available = new SimpleBooleanProperty();
    private final StringProperty bookingId  = new SimpleStringProperty("");
    private final StringProperty customerName  = new SimpleStringProperty("");
    private final StringProperty customerPhone = new SimpleStringProperty("");
    private final IntegerProperty nights    = new SimpleIntegerProperty(0);

    public Room(String num, String type, double pr, boolean av,
                String bId, String name, String phone, int n) {
        roomNumber.set(num); roomType.set(type); price.set(pr); available.set(av);
        bookingId.set(bId); customerName.set(name); customerPhone.set(phone); nights.set(n);
    }

    public String getRoomNumber()  { return roomNumber.get(); }
    public StringProperty roomNumberProperty() { return roomNumber; }
    public String getRoomType()    { return roomType.get(); }
    public StringProperty roomTypeProperty()   { return roomType; }
    public double getPrice()       { return price.get(); }
    public DoubleProperty priceProperty()      { return price; }
    public boolean getAvailable()  { return available.get(); }
    public BooleanProperty availableProperty() { return available; }
    public String getBookingId()   { return bookingId.get(); }
    public StringProperty bookingIdProperty()  { return bookingId; }
    public String getCustomerName()  { return customerName.get(); }
    public StringProperty customerNameProperty() { return customerName; }
    public String getCustomerPhone() { return customerPhone.get(); }
    public StringProperty customerPhoneProperty(){ return customerPhone; }
    public int getNights()         { return nights.get(); }
    public IntegerProperty nightsProperty()    { return nights; }

    public double getTotalPrice()  { return price.get() * nights.get(); }

    public String toCSV() {
        String b = bookingId.get().trim().isEmpty()     ? "N/A" : bookingId.get();
        String n = customerName.get().trim().isEmpty()  ? "N/A" : customerName.get();
        String p = customerPhone.get().trim().isEmpty() ? "N/A" : customerPhone.get();
        return String.format("%s,%s,%.2f,%b,%s,%s,%s,%d",
            getRoomNumber(), getRoomType(), getPrice(), getAvailable(), b, n, p, getNights());
    }
}