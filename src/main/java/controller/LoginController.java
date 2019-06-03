package controller;

import client.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;


public class LoginController {
    private Client client;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passTextField;

    public void setClient(Client client) {
        this.client = client;
    }

    public void startClient() {
        if (!client.start()) (new Alert(Alert.AlertType.ERROR, "Error starting client")).show();
    }

    @FXML
    public void handleLoginUser() throws IOException {
        String username = usernameTextField.getText();
        String pass = passTextField.getText();

        client.requestLogin(username, pass);
        while (!client.isLogged_in()) {
            if (client.isError())
                return;
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/MainWindow.fxml"));

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Main Window");
        primaryStage.setScene(new Scene(loader.load()));
        loader.<MainWindowController>getController().setClient(client);
        client.setMainWindowController(loader.getController());
        loader.<MainWindowController>getController().setExcursii();

        Stage currentStage = (Stage) usernameTextField.getScene().getWindow();

        primaryStage.setX(currentStage.getX() - currentStage.getWidth() / 2);
        primaryStage.setY(currentStage.getY() - currentStage.getHeight() / 2);

        currentStage.close();
        primaryStage.show();
    }
}

