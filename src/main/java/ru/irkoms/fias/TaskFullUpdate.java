package ru.irkoms.fias;

import com.github.junrar.extract.ExtractArchive;
import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;

public class TaskFullUpdate extends Task {
    private File arc;
    private String region;
    private String ver = "0";
    private File tmpDir = Paths.get(System.getProperty("java.io.tmpdir"), "fias-upd").toFile();

    public void setRegion(String region) {
        this.region = region;
    }

    public TaskFullUpdate(File arc) {
        this.arc = arc;
    }

    @Override
    protected Object call() throws Exception {
        updateMessage("Распаковка...");
        extract();
        updateMessage("Обработка");

        FileUtils.listFiles(tmpDir, null, false)
                .forEach(this::process);

        updateMessage("Пост-обработка");

        Connection c = Main.connUp();
        c.createStatement().execute("pragma user_version = " + ver + ";");
        c.close();

        updateMessage("Готово");
        System.out.println("...");

        return null;
    }

    private void extract() {
        try {
            if (tmpDir.exists()) {
                FileUtils.forceDelete(tmpDir);
            }
            FileUtils.forceMkdir(tmpDir);
            ExtractArchive extractArchive = new ExtractArchive();
            extractArchive.extractArchive(arc, tmpDir);
            System.out.println(tmpDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process(File f) {
        ver = f.getName().replaceAll(".*_(\\d{8})_.*", "$1"); // версия = дата в имени файла
        String type = f.getName().substring(3).replaceAll("_\\d{8}_.*", "");
        updateMessage("Обработка " + type + "...");

        if (getClass().getResource("/schema/" + type + ".txt") != null) {
            try {
                FiasHandler handler = new FiasHandler(type, region);

                // full update: drop & create table
                Connection c = Main.connUp();
                String sql = "DROP TABLE IF EXISTS " + type + "; VACUUM;";
                c.createStatement().execute(sql);
                sql = "CREATE TABLE " + type + " (" + String.join(", ", handler.getPropsDb()) + ")";
                c.createStatement().execute(sql);
                c.close();

                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                saxParser.parse(f, handler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
