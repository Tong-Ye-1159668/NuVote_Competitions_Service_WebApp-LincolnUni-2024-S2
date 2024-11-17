package lu.p2.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class Json {

    private final ObjectMapper objectMapper;

    public Json(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T read(InputStream stream, Class<T> type) {
        try {
            return objectMapper.readValue(stream, type);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
