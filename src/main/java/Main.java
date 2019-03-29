import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main extends Application {
    public static void main(String[] args) {
        Properties serverProps = new Properties();
        try {
            serverProps.load(new FileReader("bd.config"));
            //System.setProperties(serverProps);

            System.out.println("Properties set. ");
            //System.getProperties().list(System.out);
            serverProps.list(System.out);
        } catch (IOException e) {
            System.out.println("Cannot find bd.config " + e);
        }

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
    }
}
