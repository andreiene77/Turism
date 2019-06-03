package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Excursie;

public class RezervareWindowController {
    private MainWindowController mainController;

    @FXML
    private TextField numeTextField;
    @FXML
    private TextField telefonTextField;
    @FXML
    private TextField nrBileteTextField;

    void setMainController(MainWindowController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void handleRezervare() {
        Excursie exSelected = mainController.getExcursiiTableView().getSelectionModel().getSelectedItem();

        if (!(exSelected == null)) {
            String nume = numeTextField.getText();
            String nrTelefon = telefonTextField.getText();
            int bilete = Integer.valueOf(nrBileteTextField.getText());

            try {
                mainController.getClient().requestRezervare(exSelected, nume, nrTelefon, bilete);
            } catch (Exception e) {
                (new Alert(Alert.AlertType.ERROR, e.getMessage())).show();
            }
        }
    }

    @FXML
    public void handleAnulare() {
        ((Stage) numeTextField.getScene().getWindow()).close();
    }
}
