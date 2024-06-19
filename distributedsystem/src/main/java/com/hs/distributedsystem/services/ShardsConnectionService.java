package com.hs.distributedsystem.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.stereotype.Service;

import com.hs.distributedsystem.configuration.ShardsConfiguration;
import com.hs.distributedsystem.configuration.ShardsConfiguration.Shard;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ShardsConnectionService {

  @Getter
  @Setter
  private Integer shard;

  @Getter
  private Connection databaseConnection;

  private final ShardsConfiguration shardsConfiguration;

  ShardsConnectionService(ShardsConfiguration shardsConfiguration) {
    this.shardsConfiguration = shardsConfiguration;
  }

  public void setDatabaseConnection(String url, String username, String password) throws SQLException {
    this.databaseConnection = DriverManager.getConnection(url, username, password);
  }

  public void closeConnection() {
    try {
      if (databaseConnection == null || databaseConnection.isClosed())
        return;
      databaseConnection.close();
    } catch (SQLException e) {
      log.error("Error while closing connection");
    } finally {
      shard = null;
    }
  }

  private Integer getShardIndex(Object key) {
    return key.hashCode() % shardsConfiguration.getShards().size();
  }

  public Connection getShardConnection(Object key) throws SQLException {
    Integer shardIndex = getShardIndex(key);
    setShard(shardIndex);
    Shard shardConfiguration = shardsConfiguration.getShards().get(shardIndex);
    setDatabaseConnection(shardConfiguration.getUrl(), shardConfiguration.getUsername(), shardConfiguration.getPassword());
    return databaseConnection;
  }
}