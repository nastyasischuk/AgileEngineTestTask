package com.agileengine.task.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api")
public class AgileEngineApiConfig {
    @Value("${api.key}")
    private String key;
    @Value("${api.baseUri}")
    private String baseUri;
    @Value("${image.refresher.cron}")
    private String imageRefresherCron;
    @Value("${pool.size}")
    private Integer poolSize;

    public String getKey() {
        return key;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public String getCronExpression() {
        return imageRefresherCron;
    }

    public Integer getPoolSize() {
        return poolSize;
    }

}
