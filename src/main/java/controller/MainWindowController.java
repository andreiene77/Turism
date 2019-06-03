package controller;

import client.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Excursie;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

public class MainWindowController {
    private ObservableList<Excursie> excursii = FXCollections.observableArrayList();

    private Client client;

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

    static void setColumns(TableColumn<Excursie, String> obiectivColumn, TableColumn<Excursie, String> firmaColumn, TableColumn<Excursie, LocalTime> oraPlecareColumn, TableColumn<Excursie, Double> pretColumn, TableColumn<Excursie, Integer> locuriColumn) {
        obiectivColumn.setCellValueFactory(new PropertyValueFactory<>("obiectiv"));
        firmaColumn.setCellValueFactory(new PropertyValueFactory<>("firmaTransport"));
        oraPlecareColumn.setCellValueFactory(new PropertyValueFactory<>("oraPlecarii"));
        pretColumn.setCellValueFactory(new PropertyValueFactory<>("pretul"));
        locuriColumn.setCellValueFactory(new PropertyValueFactory<>("locuriDisponibile"));
    }

    static void updateTableView(Client client, ObservableList<Excursie> excursii, TableView<Excursie> excursiiTableView) {
        List<Excursie> list = client.getListExcursii();
        excursii.setAll(list);
        excursiiTableView.setItems(excursii);
    }

    TableView<Excursie> getExcursiiTableView() {
        return excursiiTableView;
    }

    Client getClient() {
        return client;
    }

    void setClient(Client client) {
        this.client = client;
    }

    void setExcursii() {
        setColumns(obiectivColumn, firmaColumn, oraPlecareColumn, pretColumn, locuriColumn);
        setRedRows();
        updateTableView(client);
    }

    private void updateTableView(Client client) {
        client.requestListExcursii();
        while (!client.isListExcursiiModified()) {
            if (client.isError())
                return;
        }
        updateTableView(client, excursii, excursiiTableView);
    }

    public void updateTableView(List<Excursie> list) {
        excursii.setAll(list);
        excursiiTableView.setItems(excursii);
    }

    private void setRedRows() {
        excursiiTableView.setRowFactory(tv -> new TableRow<Excursie>() {
            @Override
            public void updateItem(Excursie item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setStyle("");
                } else if (item.getLocuriDisponibile() == 0) {
                    setStyle("-fx-background-color: tomato;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    @FXML
    public void handleLogout() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/Login.fxml"));

        client.requestLogout();
        while (client.isLogged_in()) {
            if (client.isError())
                return;
        }

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Log in");
        primaryStage.setScene(new Scene(loader.load()));
        ((Stage) excursiiTableView.getScene().getWindow()).close();
        primaryStage.show();
        LoginController loginController = loader.getController();
        loginController.setClient(client);
    }

    @FXML
    public void handleSearch() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/SearchWindow.fxml"));

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Cautare");
        primaryStage.setScene(new Scene(loader.load()));

        loader.<SearchWindowController>getController().setClient(client);
        loader.<SearchWindowController>getController().setExcursii();

        Stage currentStage = (Stage) excursiiTableView.getScene().getWindow();

        primaryStage.setX(currentStage.getX() + currentStage.getWidth());
        primaryStage.setY(currentStage.getY());

        primaryStage.show();
    }

    @FXML
    public void handleRezervare() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/RezervareWindow.fxml"));

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Rezervare");
        primaryStage.setScene(new Scene(loader.load()));
        loader.<RezervareWindowController>getController().setMainController(this);

        Stage currentStage = (Stage) excursiiTableView.getScene().getWindow();

        primaryStage.setX(currentStage.getX());
        primaryStage.setY(currentStage.getY() + currentStage.getHeight());

        primaryStage.show();
    }

    public void handleError() {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Error from server.");
        alert.show();
    }

    public void handleRezevareSuccess() {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Rezervare Made!");
//        alert.show();
        System.out.println("Rezervare Made!");
    }
}
