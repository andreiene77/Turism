import client.Client;
import controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.Logger;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            init(primaryStage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        primaryStage.setTitle("Log In");
    }

    private void init(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/Login.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.show();
        LoginController controller = loader.getController();
        Client client = new Client("localhost", 55555);
        Logger logger = new Logger();
        client.addClientListener(logger);
        controller.setClient(client);
        controller.startClient();
    }
}
