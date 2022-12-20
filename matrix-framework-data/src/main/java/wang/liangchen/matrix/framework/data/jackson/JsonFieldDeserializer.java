package wang.liangchen.matrix.framework.data.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import wang.liangchen.matrix.framework.data.dao.entity.JsonField;

import java.io.IOException;
import java.util.Map;

/**
 * @author Liangchen.Wang 2022-12-12 14:46
 */
public class JsonFieldDeserializer extends JsonDeserializer<JsonField> {

    @Override
    public JsonField deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JacksonException {
        Map<String, Object> innerMap = jsonParser.getCodec().readValue(jsonParser, new TypeReference<Map<String, Object>>() {
        });
        return JsonField.newInstance(innerMap);
    }

}