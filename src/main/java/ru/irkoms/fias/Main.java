package ru.irkoms.fias;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Main extends Application {

    public static final Logger logger = LoggerFactory.getLogger("LG_ROOT");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("App start");
        getLastUpd();

        Parent root = FXMLLoader.load(getClass().getResource("/app.fxml"));
        primaryStage.setTitle("MyApp");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private String getLastUpd() throws IOException {
        URL url = new URL("http://fias.nalog.ru/Public/Downloads/Actual/VerDate.txt");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);
        in.close();

        return "1";
    }
}
