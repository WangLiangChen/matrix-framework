package wang.liangchen.matrix.framework.springboot.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import wang.liangchen.matrix.framework.commons.enumeration.ConstantEnum;

import java.io.IOException;

/**
 * @author Liangchen.Wang 2022-12-12 14:46
 */
public class ConstantEnumSerializer extends JsonSerializer<ConstantEnum> {
    @Override
    public void serialize(ConstantEnum constantEnum, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        jsonGenerator.writeString(constantEnum.key());
    }
}