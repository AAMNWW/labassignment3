package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HelloApplication extends Application {

    private final Map<String, String> dataMap = new HashMap<>();
    private static final String FILE_NAME = "data.txt";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Data Storage App");

        // UI Elements
        TextField nameField = new TextField();
        TextField idField = new TextField();
        DatePicker dobPicker = new DatePicker();

        ComboBox<String> provinceComboBox = new ComboBox<>();
        provinceComboBox.getItems().addAll("Punjab", "Sindh", "Khyber Pakhtunkhwa", "Balochistan");

        ToggleGroup genderGroup = new ToggleGroup();
        RadioButton maleButton = new RadioButton("Male");
        RadioButton femaleButton = new RadioButton("Female");
        RadioButton Other = new RadioButton("Other");

        maleButton.setToggleGroup(genderGroup);
        femaleButton.setToggleGroup(genderGroup);

        Button saveButton = new Button("Save");
        Button findButton = new Button("Find");
        Button closeButton = new Button("Close");
        GridPane grid = createGridPane();
        grid.addRow(0, new Label("Full Name:"), nameField);
        grid.addRow(1, new Label("ID:"), idField);
        grid.addRow(2, new Label("Gender:"), new HBox(10, maleButton, femaleButton));
        grid.addRow(3, new Label("Province:"), provinceComboBox);
        grid.addRow(4, new Label("Date of Birth:"), dobPicker);
        grid.addRow(5, saveButton, findButton);
        grid.add(closeButton, 1, 6);

        loadDataFromFile();

        // Button actions
        saveButton.setOnAction(e -> saveData(nameField, idField, genderGroup, provinceComboBox, dobPicker));
        findButton.setOnAction(e -> findData());
        closeButton.setOnAction(e -> primaryStage.close());

        // Scene
        primaryStage.setScene(new Scene(grid, 400, 300));
        primaryStage.show();
    }

    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        return grid;
    }

    private void saveData(TextField nameField, TextField idField, ToggleGroup genderGroup, ComboBox<String> provinceComboBox, DatePicker dobPicker) {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        RadioButton selectedGender = (RadioButton) genderGroup.getSelectedToggle();
        String gender = selectedGender != null ? selectedGender.getText() : "";
        String province = provinceComboBox.getValue();
        String dob = dobPicker.getValue() != null ? dobPicker.getValue().toString() : "";

        if (id.isEmpty() || name.isEmpty() || gender.isEmpty() || province == null || dob.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields are required to save data.");
            return;
        }

        String data = String.join(";", name, id, gender, province, dob);
        dataMap.put(id, data);
        saveDataToFile();

        showAlert(Alert.AlertType.INFORMATION, "Success", "Data saved successfully.");
        clearFields(nameField, idField, genderGroup, provinceComboBox, dobPicker);
    }

    private void findData() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Find Data");
        dialog.setHeaderText("Find Data by ID");
        dialog.setContentText("Enter ID:");

        dialog.showAndWait().ifPresent(id -> {
            String data = dataMap.get(id.trim());
            if (data != null) {
                String[] parts = data.split(";");
                showAlert(Alert.AlertType.INFORMATION, "Data Found", String.format(
                        "Full Name: %s\nID: %s\nGender: %s\nProvince: %s\nDate of Birth: %s",
                        parts[0], parts[1], parts[2], parts[3], parts[4]
                ));
            } else {
                showAlert(Alert.AlertType.ERROR, "Not Found", "No data found for the given ID.");
            }
        });
    }

    private void saveDataToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String data : dataMap.values()) {
                writer.write(data);
                writer.newLine();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save data to file: " + e.getMessage());
        }
    }

    private void loadDataFromFile() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";");
                    if (parts.length == 5) {
                        dataMap.put(parts[1], line);
                    }
                }
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load data from file: " + e.getMessage());
            }
        }
    }

    private void clearFields(TextField nameField, TextField idField, ToggleGroup genderGroup, ComboBox<String> provinceComboBox, DatePicker dobPicker) {
        nameField.clear();
        idField.clear();
        genderGroup.selectToggle(null);
        provinceComboBox.setValue(null);
        dobPicker.setValue(null);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
