package com.hs.loadbalancing.configuration;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

  private Services services;

  private Health health;

  @Data
  public static class Services {

    private List<String> instances;

  }

  @Data
  public static class Health {

    private Integer checkInterval;

  }
  
}
