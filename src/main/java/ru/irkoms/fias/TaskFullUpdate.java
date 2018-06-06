package ru.irkoms.fias;

import com.github.junrar.extract.ExtractArchive;
import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class TaskFullUpdate extends Task {
    private File arc;
    private File tmpDir = Paths.get(System.getProperty("java.io.tmpdir"), "fias-upd").toFile();

    public TaskFullUpdate(File arc) {
        this.arc = arc;
    }

    @Override
    protected Object call() throws Exception {
        updateMessage("Распаковка...");
//        extract();
        updateMessage("Готово");

        FileUtils.listFiles(tmpDir, null, false)
                .forEach(this::process);

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
            System.out.println(arc.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process(File f) {
        String typ = f.getName().replaceAll("_\\d{8}_.*", "");

        if (typ.equals("AS_DEL_ADDROBJ")) {
            System.out.println(f.getAbsolutePath());

            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();

                saxParser.parse(f, new FiasHandler());

            } catch (SAXException | ParserConfigurationException | IOException e) {
                e.printStackTrace();
            }

        }
    }
}
