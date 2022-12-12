package com.haoliang.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppParamProperties {

    private String rootPath;

    private String serverName;

    private String virtualPathPrefix;

    private Integer fileMaxSize;

    private String version;

    private String imageSavePath;

    private String sysfileSavePath;

    private List<String> securityExcludes;

}
