package wang.liangchen.matrix.framework.spring.boot.configuration;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import wang.liangchen.matrix.framework.commons.datetime.DateTimeUtil;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author Liangchen.Wang
 */
@AutoConfiguration
public class SpringBootAutoConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(BigInteger.class, ToStringSerializer.instance);
            builder.simpleDateFormat(DateTimeUtil.DEFAULT_DATETIME_STRING);
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeUtil.DEFAULT_DATETIME_FORMATTER));
            builder.serializerByType(LocalDate.class, new LocalDateSerializer(DateTimeUtil.DEFAULT_DATE_FORMATTER));
            builder.serializerByType(LocalTime.class, new LocalDateSerializer(DateTimeUtil.DEFAULT_TIME_FORMATTER));
            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeUtil.DEFAULT_DATETIME_FORMATTER));
            builder.deserializerByType(LocalDate.class, new LocalDateTimeDeserializer(DateTimeUtil.DEFAULT_DATE_FORMATTER));
            builder.deserializerByType(LocalTime.class, new LocalDateTimeDeserializer(DateTimeUtil.DEFAULT_TIME_FORMATTER));
        };

    }
}
