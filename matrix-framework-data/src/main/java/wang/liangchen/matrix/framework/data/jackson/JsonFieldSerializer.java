package wang.liangchen.matrix.framework.data.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import wang.liangchen.matrix.framework.data.dao.entity.JsonField;

import java.io.IOException;

/**
 * @author Liangchen.Wang 2022-12-12 14:46
 */
public class JsonFieldSerializer extends JsonSerializer<JsonField> {
    @Override
    public void serialize(JsonField jsonField, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        jsonGenerator.writeObject(jsonField.getNativeMap());
    }
}