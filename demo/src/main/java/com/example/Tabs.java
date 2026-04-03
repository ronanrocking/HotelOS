package com.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Tabs {

    private static final String COLOR_NAVY = "#1a2a3a";

    // --- TAB 1: ROOMS ---
    @SuppressWarnings("unchecked")
    public static Tab createRoomsTab(ObservableList<Room> roomData) {
        VBox content = new VBox(25);
        content.setPadding(new Insets(30));

        Label heading = new Label("ROOM DETAILS");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_NAVY + ";");

        Button showAll   = UiHelper.createActionButton("View All Rooms", "#3498db");
        Button showAvail = UiHelper.createActionButton("Available Only",  "#34495e");
        HBox filterBar = new HBox(15, new Label("Quick Filters:"), showAll, showAvail);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        FilteredList<Room> filteredRooms = new FilteredList<>(roomData, r -> true);
        showAll.setOnAction(e -> filteredRooms.setPredicate(r -> true));
        showAvail.setOnAction(e -> filteredRooms.setPredicate(Room::getAvailable));

        TableView<Room> table = new TableView<>(filteredRooms);
        table.getColumns().addAll(
            UiHelper.createCol("Room #",        "roomNumber"),
            UiHelper.createCol("Type",          "roomType"),
            UiHelper.createCol("Price / Night", "price"),
            UiHelper.createCol("Availability",  "available")
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        content.getChildren().addAll(heading, filterBar, table);
        return new Tab("Rooms", content);
    }

    // --- TAB 2: BOOKINGS ---
    @SuppressWarnings("unchecked")
    public static Tab createBookingsTab(ObservableList<Room> roomData) {
        VBox content = new VBox(25);
        content.setPadding(new Insets(30));

        Label heading = new Label("ACTIVE RESERVATIONS");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_NAVY + ";");

        FilteredList<Room> occupied = new FilteredList<>(roomData, r -> !r.getAvailable());
        TableView<Room> table = new TableView<>(occupied);

        TableColumn<Room, String> billCol = new TableColumn<>("Total Bill");
        billCol.setCellValueFactory(f ->
            new SimpleStringProperty(String.format("₹ %.2f", f.getValue().getTotalPrice())));

        table.getColumns().addAll(
            UiHelper.createCol("Booking ID",    "bookingId"),
            UiHelper.createCol("Room #",        "roomNumber"),
            UiHelper.createCol("Guest Name",    "customerName"),
            UiHelper.createCol("Contact Info",  "customerPhone"),
            UiHelper.createCol("Stay (Nights)", "nights"),
            billCol
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        content.getChildren().addAll(heading, table);
        return new Tab("Bookings", content);
    }

    // --- TAB 3: ADD ROOM ---
    public static Tab createAddRoomTab(ObservableList<Room> roomData, CsvManager csv) {
        VBox content = new VBox(30);
        content.setPadding(new Insets(30));

        UiHelper.FormContainer addForm = new UiHelper.FormContainer(
            "ROOM REGISTRY", "Add new rooms to the system inventory");
        GridPane grid = new GridPane(); grid.setHgap(15); grid.setVgap(12);

        TextField num = new TextField(), price = new TextField();
        UiHelper.addNumericFilter(num); UiHelper.addNumericFilter(price);
        ComboBox<String> type = new ComboBox<>(
            FXCollections.observableArrayList("Standard", "Double", "Suite", "Deluxe"));
        Button addBtn = UiHelper.createActionButton("Add to Inventory", "#27ae60");

        grid.addRow(0, new Label("Room Number:"),    num);
        grid.addRow(1, new Label("Room Type:"),      type);
        grid.addRow(2, new Label("Price per Night:"), price);
        grid.add(addBtn, 1, 3);
        addForm.getChildren().add(grid);

        addBtn.setOnAction(e -> {
            if (num.getText().isEmpty() || price.getText().isEmpty() || type.getValue() == null) {
                UiHelper.showError("Input Error", "Please fill all fields."); return;
            }
            if (roomData.stream().anyMatch(r -> r.getRoomNumber().equals(num.getText()))) {
                UiHelper.showError("Duplicate", "This Room Number already exists."); return;
            }
            roomData.add(new Room(num.getText(), type.getValue(),
                Double.parseDouble(price.getText()), true, "", "", "", 0));
            csv.save(roomData);
            UiHelper.showConfirm("Success", "Room added successfully.");
            num.clear(); price.clear();
        });

        UiHelper.FormContainer delForm = new UiHelper.FormContainer(
            "DELETE ROOM", "Permanently remove a vacant room");
        TextField delNum = new TextField(); delNum.setPromptText("Room #");
        Button delBtn = UiHelper.createActionButton("Delete Room", "#c0392b");
        delForm.getChildren().add(new HBox(15, delNum, delBtn));

        delBtn.setOnAction(e -> {
            roomData.stream()
                .filter(r -> r.getRoomNumber().equals(delNum.getText()))
                .findFirst()
                .ifPresentOrElse(r -> {
                    if (r.getAvailable()) {
                        roomData.remove(r); csv.save(roomData);
                        UiHelper.showConfirm("Deleted", "Room removed.");
                        delNum.clear();
                    } else UiHelper.showError("Denied", "Cannot delete occupied rooms.");
                }, () -> UiHelper.showError("Error", "Room not found."));
        });

        content.getChildren().addAll(addForm, new Separator(), delForm);
        return new Tab("Add Room", content);
    }

    // --- TAB 4: FRONT DESK ---
    public static Tab createFrontDeskTab(ObservableList<Room> roomData, CsvManager csv) {
        VBox content = new VBox(30);
        content.setPadding(new Insets(30));

        UiHelper.FormContainer bookForm = new UiHelper.FormContainer(
            "GUEST CHECK-IN", "Assign guest and booking details");
        GridPane grid = new GridPane(); grid.setHgap(15); grid.setVgap(12);

        TextField bId = new TextField(), rNum = new TextField(),
                  name = new TextField(), phone = new TextField(),
                  nights = new TextField();
        UiHelper.addNumericFilter(nights);
        Button bBtn = UiHelper.createActionButton("Confirm Booking", "#27ae60");

        grid.addRow(0, new Label("Booking ID:"),       bId);
        grid.addRow(1, new Label("Room #:"),           rNum);
        grid.addRow(2, new Label("Guest Name:"),       name);
        grid.addRow(3, new Label("Contact #:"),        phone);
        grid.addRow(4, new Label("Duration (Nights):"), nights);
        grid.add(bBtn, 1, 5);
        bookForm.getChildren().add(grid);

        bBtn.setOnAction(e -> {
            if (bId.getText().isEmpty() || rNum.getText().isEmpty() || name.getText().isEmpty()) {
                UiHelper.showError("Input Error", "Please fill all required fields."); return;
            }
            roomData.stream()
                .filter(r -> r.getRoomNumber().equals(rNum.getText()))
                .findFirst()
                .ifPresentOrElse(r -> {
                    if (r.getAvailable()) {
                        r.availableProperty().set(false);
                        r.bookingIdProperty().set(bId.getText());
                        r.customerNameProperty().set(name.getText());
                        r.customerPhoneProperty().set(phone.getText());
                        r.nightsProperty().set(Integer.parseInt(nights.getText()));
                        csv.save(roomData);
                        UiHelper.showConfirm("Confirmed", "Guest checked in successfully.");
                        bId.clear(); rNum.clear(); name.clear(); phone.clear(); nights.clear();
                    } else UiHelper.showError("Occupied", "Room is currently unavailable.");
                }, () -> UiHelper.showError("Error", "Room not found."));
        });

        UiHelper.FormContainer outForm = new UiHelper.FormContainer(
            "GUEST CHECK-OUT", "Generate bill and vacate room");
        TextField outNum = new TextField();
        Button outBtn = UiHelper.createActionButton("Process Checkout", "#c0392b");
        outForm.getChildren().add(new HBox(15, outNum, outBtn));

        outBtn.setOnAction(e -> {
            roomData.stream()
                .filter(r -> r.getRoomNumber().equals(outNum.getText()))
                .findFirst()
                .ifPresentOrElse(r -> {
                    if (!r.getAvailable()) {
                        UiHelper.showConfirm("Final Bill",
                            "Guest: " + r.getCustomerName() + "\nTotal: ₹" + r.getTotalPrice());
                        r.availableProperty().set(true);
                        r.bookingIdProperty().set("");
                        r.customerNameProperty().set("");
                        r.customerPhoneProperty().set("");
                        r.nightsProperty().set(0);
                        csv.save(roomData); outNum.clear();
                    } else UiHelper.showError("Empty", "Room is already vacant.");
                }, () -> UiHelper.showError("Error", "Invalid Room #."));
        });

        content.getChildren().addAll(bookForm, new Separator(), outForm);
        return new Tab("Guest Details", content);
    }
}