package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Agentie;
import model.Excursie;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.ExcursiiManagementService;

import java.io.IOException;
import java.sql.Time;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MainWindowController {
    private ExcursiiManagementService excursiiManagementService;

    private ObservableList<Excursie> excursii = FXCollections.observableArrayList();

    @FXML
    private TableView<Excursie> excursiiTableView;
    @FXML
    private TableColumn<Excursie, String> obiectivColumn;
    @FXML
    private TableColumn<Excursie, String> firmaColumn;
    @FXML
    private TableColumn<Excursie, Time> oraPlecareColumn;
    @FXML
    private TableColumn<Excursie, Double> pretColumn;
    @FXML
    private TableColumn<Excursie, Integer> locuriColumn;

    public void setUser(Agentie user) {
        excursiiManagementService.setUserCurent(user);
    }

    @FXML
    public void initialize() {
        ApplicationContext context = new ClassPathXmlApplicationContext("TurismApp.xml");
        excursiiManagementService = context.getBean(ExcursiiManagementService.class);

        List<Excursie> list = StreamSupport.stream(excursiiManagementService.getAllExcursii().spliterator(), false)
                .collect(Collectors.toList());

        excursii.setAll(list);


        obiectivColumn.setCellValueFactory(new PropertyValueFactory<>("obiectiv"));
        firmaColumn.setCellValueFactory(new PropertyValueFactory<>("firmaTransport"));
        oraPlecareColumn.setCellValueFactory(new PropertyValueFactory<>("oraPlecarii"));
        pretColumn.setCellValueFactory(new PropertyValueFactory<>("pretul"));
        locuriColumn.setCellValueFactory(new PropertyValueFactory<>("locuriDisponibile"));

        excursiiTableView.setItems(excursii);
    }

    @FXML
    public void handleLogout() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/Login.fxml"));

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Log in");
        primaryStage.setScene(new Scene(loader.load()));
        ((Stage) excursiiTableView.getScene().getWindow()).close();
        primaryStage.show();
    }
}
