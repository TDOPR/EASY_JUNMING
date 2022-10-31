package com.haoliang.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppParam {

    private String rootPath;

    private String serverName;

    private String rateLimitModel;

    private List<String> securityExcludes;

}
