package wang.liangchen.matrix.framework.springboot.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import wang.liangchen.matrix.framework.commons.datetime.DateTimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Liangchen.Wang 2022-12-12 17:25
 */
public enum DefaultObjectMapper {
    INSTANCE;
    private final ObjectMapper objectMapper;
    private final TypeFactory typeFactory;

    DefaultObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Long.class, ToStringSerializer.instance);
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeUtil.DEFAULT_DATETIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeUtil.DEFAULT_DATETIME_FORMATTER));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeUtil.DEFAULT_DATE_FORMATTER));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeUtil.DEFAULT_DATE_FORMATTER));
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
}
