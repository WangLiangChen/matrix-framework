package wang.liangchen.matrix.framework.commons.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import wang.liangchen.matrix.framework.commons.datetime.DateTimeUtil;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Liangchen.Wang 2022-12-12 17:25
 */
public enum DefaultObjectMapper {
    INSTANCE;
    private ObjectMapper objectMapper;
    private TypeFactory typeFactory;

    public void resetObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.typeFactory = this.objectMapper.getTypeFactory();
    }

    DefaultObjectMapper() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Long.class, ToStringSerializer.instance);
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeUtil.DEFAULT_DATETIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeUtil.DEFAULT_DATETIME_FORMATTER));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeUtil.DEFAULT_DATE_FORMATTER));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeUtil.DEFAULT_DATE_FORMATTER));
        ObjectMapper objectMapper = JsonMapper.builder()
                .configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true)
                //.disable(MapperFeature.USE_ANNOTATIONS)
                .build();
        objectMapper.registerModule(javaTimeModule);
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
