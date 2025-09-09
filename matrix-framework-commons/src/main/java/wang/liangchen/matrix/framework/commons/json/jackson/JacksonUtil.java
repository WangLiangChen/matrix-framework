package wang.liangchen.matrix.framework.commons.json.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

/**
 * @author Liangchen.Wang 2022-12-12 17:25
 */
public enum JacksonUtil {
    INSTANCE;
    private ObjectMapper objectMapper;
    private TypeFactory typeFactory;

    public void resetObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.findAndRegisterModules();
        this.typeFactory = this.objectMapper.getTypeFactory();
    }

    JacksonUtil() {
        ObjectMapper objectMapper = JsonMapper.builder().configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true).build();
        resetObjectMapper(objectMapper);
    }

    public ObjectMapper objectMapper() {
        return this.objectMapper;
    }

    public TypeFactory typeFactory() {
        return this.typeFactory;
    }

    public String writeValueAsString(Object object) {
        try {
            return this.objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new MatrixErrorException(e);
        }
    }

    public <T> T readValue(String value, Class<T> valueType) {
        try {
            return this.objectMapper.readValue(value, valueType);
        } catch (JsonProcessingException e) {
            throw new MatrixErrorException(e);
        }
    }

    public <T> T readValue(String value, JavaType valueType) {
        try {
            return this.objectMapper.readValue(value, valueType);
        } catch (JsonProcessingException e) {
            throw new MatrixErrorException(e);
        }
    }

    public <T> T readValue(String value, TypeReference<T> typeReference) {
        try {
            return this.objectMapper.readValue(value, typeReference);
        } catch (JsonProcessingException e) {
            throw new MatrixErrorException(e);
        }
    }
}
