package com.example;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    private final ObservableList<Room> roomData = FXCollections.observableArrayList(
        room -> new Observable[]{ room.availableProperty(), room.bookingIdProperty() }
    );
    private final CsvManager csvManager = new CsvManager("Rooms.csv");
    private static final String COLOR_NAVY = "#1a2a3a";

    @Override
    public void start(Stage stage) {
        csvManager.load(roomData);

        // 1. HEADER
        Label title = new Label("🏨  HOTEL MANAGEMENT SYSTEM");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");
        HBox header = new HBox(title);
        header.setStyle("-fx-background-color: " + COLOR_NAVY + "; -fx-padding: 25;");
        header.setAlignment(Pos.CENTER);
        header.setMaxWidth(Double.MAX_VALUE);

        // 2. TAB PANE
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
            Tabs.createRoomsTab(roomData),
            Tabs.createBookingsTab(roomData),
            Tabs.createAddRoomTab(roomData, csvManager),
            Tabs.createFrontDeskTab(roomData, csvManager)
        );
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // 3. FOOTER
        Label footerText = new Label("Ronan Madan | OSDL Lab Project");
        footerText.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #bdc3c7;");
        HBox footer = new HBox(footerText);
        footer.setStyle("-fx-background-color: " + COLOR_NAVY + "; -fx-padding: 12;");
        footer.setAlignment(Pos.CENTER);
        footer.setMaxWidth(Double.MAX_VALUE);

        VBox root = new VBox(header, tabPane, footer);
        Scene scene = new Scene(root, 1150, 800);
        stage.setTitle("HotelOS - Ronan Madan");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}