package xiaoDu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * A GUI for XiaoDu using FXML.
 */
public class Main extends Application {
    private xiaoDu xiaoDu = new xiaoDu("./data/xiaoDu.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("XiaoDu");
            fxmlLoader.<MainWindow>getController().setXiaoDu(xiaoDu);  // inject the XiaoDu instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}