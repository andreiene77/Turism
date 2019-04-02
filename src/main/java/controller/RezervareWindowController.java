package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Excursie;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.RezervareManagementService;

public class RezervareWindowController {
    private RezervareManagementService rezervareManagementService;
    private MainWindowController mainController;

    @FXML
    private TextField numeTextField;
    @FXML
    private TextField telefonTextField;
    @FXML
    private TextField nrBileteTextField;

    @FXML
    public void initialize() {
        ApplicationContext context = new ClassPathXmlApplicationContext("TurismApp.xml");
        rezervareManagementService = context.getBean(RezervareManagementService.class);
    }

    public void setMainController(MainWindowController mainController) {
        this.mainController = mainController;
        rezervareManagementService.setUserCurent(mainController.getUserCurent());
    }

    @FXML
    public void handleRezervare() {
        Excursie exSelected = mainController.getExcursiiTableView().getSelectionModel().getSelectedItem();

        if (!(exSelected == null)) {
            String nume = numeTextField.getText();
            String numarTelefon = telefonTextField.getText();
            int bilete = Integer.valueOf(nrBileteTextField.getText());

            try {
                rezervareManagementService.rezerva(exSelected, nume, numarTelefon, bilete);
                mainController.update();
//                if (exSelected.getLocuriDisponibile() == 0)

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.show();
                ;
            }
        }

    }

    @FXML
    public void handleAnulare() {
        ((Stage) numeTextField.getScene().getWindow()).close();
    }
}
