package com.hs.distributedsystem.configuration; 

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "application")
public class ShardsConfiguration {

    private List<Shard> shards = new ArrayList<>();

    @Data
    public static class Shard {
        private String url;
        private String username;
        private String password;
    }

}