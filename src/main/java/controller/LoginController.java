package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Agentie;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.LoginService;

import java.io.IOException;


public class LoginController {
    private LoginService loginService;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passTextField;

    @FXML
    public void initialize() {
        ApplicationContext context = new ClassPathXmlApplicationContext("TurismApp.xml");
        loginService = context.getBean(LoginService.class);
    }

    @FXML
    public void handleLoginUser() throws IOException {

        String username = usernameTextField.getText();
        String pass = passTextField.getText();
        Agentie user = loginService.LoginUser(username, pass);

        if (user == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "WRONG USERNAME OR PASSWORD");
            alert.show();
        } else {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/MainWindow.fxml"));

            Stage primaryStage = new Stage();
            primaryStage.setTitle("Main Window");
            primaryStage.setScene(new Scene(loader.load()));
            loader.<MainWindowController>getController().setUser(user);
            ((Stage) usernameTextField.getScene().getWindow()).close();
            primaryStage.show();
        }
    }
}

