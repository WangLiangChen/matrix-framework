package wang.liangchen.matrix.framework.commons.json.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import wang.liangchen.matrix.framework.commons.object.Dictionary;

import java.io.IOException;
import java.util.Map;

/**
 * @author Liangchen.Wang 2022-12-12 14:46
 */
public class JsonFieldDeserializer extends JsonDeserializer<Dictionary> {

    @Override
    public Dictionary deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JacksonException {
        Map<String, Object> innerMap = jsonParser.getCodec().readValue(jsonParser, new TypeReference<>() {
        });
        return Dictionary.newInstance(innerMap);
    }

}