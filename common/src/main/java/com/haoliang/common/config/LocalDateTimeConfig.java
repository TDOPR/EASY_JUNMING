package com.haoliang.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author Dominick Li
 * @Description 日期格式化
 * @CreateTime 2022/11/15 15:34
 **/
@Configuration
public class LocalDateTimeConfig {

    @Primary
    @Bean
    public ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 反序列化，未知字段不失败
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 序列化，空bean不失败，被 @JsonIgnore 注解的bean
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.registerModule(new SimpleModule());
        // 默认 LocalDateTime 格式,主要是要注入这个JavaTimeModule
        JavaTimeModule timeModule = new JavaTimeModule();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
//        timeModule.addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
//        timeModule.addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE);
        mapper.registerModule(timeModule);
        return mapper;
    }
}
