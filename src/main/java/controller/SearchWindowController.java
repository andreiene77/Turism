package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Excursie;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.ExcursiiManagementService;

import java.sql.Time;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SearchWindowController {
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

    @FXML
    private TextField obiectivTextField;
    @FXML
    private TextField oraMinTextField;
    @FXML
    private TextField oraMaxTextField;

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
    public void handleSearch() {
        String obiectiv = obiectivTextField.getText();
        Time oraMin, oraMax;
        try {
            oraMin = Time.valueOf(oraMinTextField.getText());
        } catch (IllegalArgumentException e) {
            oraMin = Time.valueOf("00:00:00");
        }
        try {
            oraMax = Time.valueOf(oraMaxTextField.getText());
        } catch (IllegalArgumentException e) {
            oraMax = Time.valueOf("23:59:59");
        }

        List<Excursie> list = StreamSupport.stream(excursiiManagementService.searchExcursii(obiectiv, oraMin, oraMax).spliterator(), false)
                .collect(Collectors.toList());
        excursii.setAll(list);
    }

    @FXML
    public void handleInapoi() {
        ((Stage) excursiiTableView.getScene().getWindow()).close();
    }
}
