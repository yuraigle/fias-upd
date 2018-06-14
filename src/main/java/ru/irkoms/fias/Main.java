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
        logger.info("App start");
//
//        Connection c = Main.connUp();
//        String sql = "pragma user_version = 20180528";
//        System.out.println(sql);
//        c.createStatement().execute(sql);
//        c.commit();
//        c.close();

        Parent root = FXMLLoader.load(getClass().getResource("/app.fxml"));
        primaryStage.setTitle("Обновление ФИАС");
        primaryStage.setScene(new Scene(root, 390, 170));
        primaryStage.centerOnScreen();
        primaryStage.show();
    }


}
