package lu.p2.io;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class Yaml {

    private final YAMLMapper yamlMapper;

    public Yaml(YAMLMapper yamlMapper) {
        this.yamlMapper = yamlMapper;
    }

    public <T> T read(InputStream stream, Class<T> type) {
        try {
            return yamlMapper.readValue(stream, type);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
