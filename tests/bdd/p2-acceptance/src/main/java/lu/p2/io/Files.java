package lu.p2.io;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class Files {

    public InputStream toStream(String file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public List<InputStream> getFiles(File folder) {
        List<String> filenames = Arrays.asList(folder.list());
        List<InputStream> streams = new ArrayList<>();

        filenames.forEach(n -> {
            try {
                streams.add(new FileInputStream(String.valueOf(Paths.get(folder.getAbsolutePath(), n))));
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        });

        return streams;
    }
}