package com.example;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class UiHelper {

    public static <T> TableColumn<Room, T> createCol(String name, String prop) {
        TableColumn<Room, T> col = new TableColumn<>(name);
        col.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>(prop));
        return col;
    }

    public static Button createActionButton(String text, String color) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + color +
                   "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20;");
        return b;
    }

    public static void addNumericFilter(TextField tf) {
        tf.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("\\d*(\\.\\d*)?")) tf.setText(oldV);
        });
    }

    public static void showConfirm(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setTitle(title); a.setHeaderText(null); a.showAndWait();
    }

    public static void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.setTitle(title); a.setHeaderText(null); a.showAndWait();
    }

    // Moved from inner class to top-level in its own file or kept here
    public static class FormContainer extends VBox {
        public FormContainer(String title, String sub) {
            super(10);
            setPadding(new Insets(20));
            setStyle("-fx-background-color: white; -fx-border-color: #dcdde1; -fx-border-radius: 5;");
            Label t = new Label(title);
            t.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2f3640;");
            Label s = new Label(sub);
            s.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
            getChildren().addAll(t, s, new Separator());
        }
    }
}