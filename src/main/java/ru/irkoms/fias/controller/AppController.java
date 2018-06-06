package ru.irkoms.fias.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.irkoms.fias.Main;
import ru.irkoms.fias.TaskFullUpdate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class AppController {
    @FXML
    Label lblCurrent, lblActual, lblStatus;

    public void initialize() {
        try {
            String ver = getLastUpd();
            lblActual.setText("Актуальная на сайте ФИАС: " + ver);
        } catch (IOException e) {
            Main.logger.error("IO error at http://fias.nalog.ru/");
            lblActual.setText("Актуальная на сайте ФИАС: <ошибка подключения>");
        }
    }

    private String getLastUpd() throws IOException {
        URL url = new URL("http://fias.nalog.ru/Public/Downloads/Actual/VerDate.txt");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()));

        String ver = in.readLine();
        in.close();

        return ver;
    }

    private File askFile(String title, String mask) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Архив", mask)
        );

        // я храню эти промежуточные файлы на раб. столе, пусть все так делают
        File dir = new File(System.getProperty("user.home") + "/Desktop");
        chooser.setInitialDirectory(dir);

        Stage mainStage = (Stage) lblActual.getScene().getWindow();
        return chooser.showOpenDialog(mainStage);
    }

    public void actFull() {
        File rar = askFile("Выберите файл ФИАС.XML.RAR", "*.rar");
        if (rar != null) {
            TaskFullUpdate tsk = new TaskFullUpdate(rar);
            tsk.messageProperty().addListener((obs, v1, v2) -> lblStatus.setText(v2));
            new Thread(tsk).start();
        }

        Main.logger.info("full update");
    }

    public void actDelta() {
        Main.logger.info("delta update");
    }

}
