package lu.p2.io;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.fasterxml.jackson.dataformat.csv.CsvSchema.emptySchema;

@Component
public class Csv {

    private final CsvMapper csvMapper;
    private CsvSchema csvSchema;
    private ObjectReader objectReader;

    public Csv(CsvMapper csvMapper) {
        this.csvMapper = csvMapper;
        this.csvSchema = emptySchema().withHeader();
        this.objectReader = csvMapper.readerFor(Map.class).with(csvSchema);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> read(InputStream stream, Class<T> type) {
        try {
            return (List<T>) csvMapper.readerFor(type).with(csvSchema).readValues(stream).readAll();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> read(List<InputStream> streams, Class<T> type) {
        try {
            List<T> resultList = new ArrayList<>();

            for (InputStream stream : streams) {
                List<T> list = (List<T>) csvMapper.readerFor(type).with(csvSchema).readValues(stream).readAll();
                resultList.addAll(list);
            }

            return resultList;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public List<Map<String, String>> readGeneric(InputStream stream) {
        try {
            MappingIterator<Map<String, String>> mapMappingIterator = objectReader.readValues(stream);

            return mapMappingIterator.readAll();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Set<String> readHeaders(InputStream stream) {
        try {
            MappingIterator<Map<String, String>> mapMappingIterator = objectReader.readValues(stream);

            return mapMappingIterator.next().keySet();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
