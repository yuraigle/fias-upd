package ru.irkoms.fias;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main extends Application {

    public static final Logger logger = LoggerFactory.getLogger("LG_ROOT");

    public static Connection connUp() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:fias.db");
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Connection c;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:fias.db");
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");

        logger.info("App start");

        Parent root = FXMLLoader.load(getClass().getResource("/app.fxml"));
        primaryStage.setTitle("Обновление ФИАС");
        primaryStage.setScene(new Scene(root, 490, 135));
        primaryStage.centerOnScreen();
        primaryStage.show();
    }


}
