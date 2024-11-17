package lu.p2.io;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class FileReader {

    @SuppressWarnings("ConstantConditions")
    public String toString(String fileName) throws IOException {
        return IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName), UTF_8);
    }
}
