package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.ZoneId;

public class MainApplication extends Application{
    public static void main(String[] args) {
        launch(args);

        // System.out.println("Client Scheduler Application launched.");

        // JDBC.openConnection();
        // JDBC.closeConnection();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        stage.setTitle("Client Scheduler Application Login");
        stage.setScene(new Scene(root, 400, 250));
        stage.show();

        System.out.println("ZoneID: "+ ZoneId.systemDefault());
    }
}