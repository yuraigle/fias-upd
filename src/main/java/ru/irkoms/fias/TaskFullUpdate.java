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
    private File tmpDir = Paths.get(System.getProperty("java.io.tmpdir"), "fias-upd").toFile();

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
        updateMessage("Готово");

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process(File f) {
        String type = f.getName().substring(3).replaceAll("_\\d{8}_.*", "");
        updateMessage("Обработка " + type + "...");

        if (type.equals("ADDROBJ")) {
            try {
                FiasHandler handler = new FiasHandler(type);

                // full update: drop & create table
                Connection c = Main.connUp();
                String sql = "DROP TABLE IF EXISTS " + type + "; ";
                c.createStatement().execute(sql);
                sql = "CREATE TABLE " + type + " (" +
                        String.join(" TEXT, ", handler.getProps()) + " TEXT)";
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
