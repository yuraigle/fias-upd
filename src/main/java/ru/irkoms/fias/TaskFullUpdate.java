package ru.irkoms.fias;

import com.github.junrar.extract.ExtractArchive;
import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class TaskFullUpdate extends Task {
    File arc;

    public TaskFullUpdate(File arc) {
        this.arc = arc;
    }

    @Override
    protected Object call() throws Exception {
        updateMessage("Распаковка...");
//        extract();
        updateMessage("Готово");

        return null;
    }

    private void extract() {
        File tmpDir = Paths.get(System.getProperty("java.io.tmpdir"), "fias-upd").toFile();

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

    private void process() {

    }
}
