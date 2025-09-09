package wang.liangchen.matrix.framework.commons.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import wang.liangchen.matrix.framework.commons.object.Dictionary;

import java.io.IOException;

/**
 * @author Liangchen.Wang 2022-12-12 14:46
 */
public class JsonFieldSerializer extends JsonSerializer<Dictionary> {
    @Override
    public void serialize(Dictionary dictionary, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        jsonGenerator.writeObject(dictionary.nativeMap());
    }
}