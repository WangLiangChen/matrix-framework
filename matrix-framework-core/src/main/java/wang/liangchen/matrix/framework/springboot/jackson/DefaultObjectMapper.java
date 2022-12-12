package wang.liangchen.matrix.framework.springboot.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * @author Liangchen.Wang 2022-12-12 17:25
 */
public enum DefaultObjectMapper {
    INSTANCE;
    private final ObjectMapper objectMapper;
    private final TypeFactory typeFactory;

    DefaultObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        this.objectMapper = objectMapper;
        this.typeFactory = this.objectMapper.getTypeFactory();
    }

    public ObjectMapper objectMapper() {
        return this.objectMapper;
    }

    public TypeFactory typeFactory() {
        return this.typeFactory;
    }
}
