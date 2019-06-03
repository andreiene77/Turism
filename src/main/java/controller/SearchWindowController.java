package controller;

import client.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Excursie;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SearchWindowController {
    private Client client;
    private ObservableList<Excursie> excursii = FXCollections.observableArrayList();

    @FXML
    private TableView<Excursie> excursiiTableView;
    @FXML
    private TableColumn<Excursie, String> obiectivColumn;
    @FXML
    private TableColumn<Excursie, String> firmaColumn;
    @FXML
    private TableColumn<Excursie, LocalTime> oraPlecareColumn;
    @FXML
    private TableColumn<Excursie, Double> pretColumn;
    @FXML
    private TableColumn<Excursie, Integer> locuriColumn;

    @FXML
    private TextField obiectivTextField;
    @FXML
    private TextField oraMinTextField;
    @FXML
    private TextField oraMaxTextField;

    void setExcursii() {
        MainWindowController.updateTableView(client, excursii, excursiiTableView);
        MainWindowController.setColumns(obiectivColumn, firmaColumn, oraPlecareColumn, pretColumn, locuriColumn);
        excursiiTableView.setItems(excursii);
    }

    void setClient(Client client) {
        this.client = client;
    }

    @FXML
    public void handleSearch() {
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("HH:mm");
        String obiectiv = obiectivTextField.getText();
        LocalTime oraMin, oraMax;
        try {
            oraMin = LocalTime.parse(oraMinTextField.getText(), parser);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            oraMin = LocalTime.MIN;
        }
        try {
            oraMax = LocalTime.parse(oraMaxTextField.getText(), parser);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            oraMax = LocalTime.MAX;
        }

        client.requestSearchList(obiectiv, oraMin.format(parser), oraMax.format(parser));
        while (!client.isListSearchModified()) {
            if (client.isError())
                return;
        }
        List<Excursie> list = client.getListSearchExcursii();
        excursii.setAll(list);
    }

    @FXML
    public void handleInapoi() {
        ((Stage) excursiiTableView.getScene().getWindow()).close();
    }
}
