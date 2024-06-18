package com.hs.distributedsystem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.hs.distributedsystem.services.TransactionService;
import com.hs.distributedsystem.services.dto.TransactionDTO;

@Testcontainers
@SpringBootTest
class DistributedsystemApplicationTests {

	@Container
	static PostgreSQLContainer<?> postgreSQLContainer1 = new PostgreSQLContainer<>("postgres:16")
			.withDatabaseName("ds0")
			.withUsername("test")
			.withPassword("test");

	@Container
	static PostgreSQLContainer<?> postgreSQLContainer2 = new PostgreSQLContainer<>("postgres:16")
			.withDatabaseName("ds1")
			.withUsername("test")
			.withPassword("test");

	static {
		postgreSQLContainer2.setPortBindings(List.of("54320:5432"));
		postgreSQLContainer1.setPortBindings(List.of("54321:5432"));
	}

	@Autowired
	private TransactionService transactionService;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
	}

	@Test
	void shouldFindOrderInCorrectShard() {
		// given
		TransactionDTO transactionDTO1 = new TransactionDTO();
		transactionDTO1.setAmount(BigDecimal.TEN);
		transactionDTO1.setCurrency("USD");
		transactionDTO1.setDescription("Hello, World");
		transactionDTO1.setUserId("user-1");

		TransactionDTO transactionDTO2 = new TransactionDTO();
		transactionDTO2.setAmount(BigDecimal.valueOf(12.5));
		transactionDTO2.setCurrency("EUR");
		transactionDTO2.setDescription("Hello, World");
		transactionDTO2.setUserId("user-2");

		// when
		TransactionDTO savedTransaction1 = transactionService.save(transactionDTO1);
		TransactionDTO savedTransaction2 = transactionService.save(transactionDTO2);

		// then
		// Assuming the sharding strategy is based on the transaction id, data for transactionDTO1
		// should be present only in ds0 and data for transactionDTO2 should be present only in ds1
		Assertions.assertThat(transactionService.findById(savedTransaction1.getId())).contains(savedTransaction1);
		Assertions.assertThat(transactionService.findById(savedTransaction2.getId())).contains(savedTransaction2);

		// Verify that the transactions are not present in the wrong shards.
		Assertions.assertThat(assertTransactionInShard(savedTransaction1, postgreSQLContainer2)).isTrue();
		Assertions.assertThat(assertTransactionInShard(savedTransaction2, postgreSQLContainer1)).isTrue();
	}

	private boolean assertTransactionInShard(TransactionDTO transactionDTO, PostgreSQLContainer<?> container) {
		try (Connection conn = DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword())) {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM transaction WHERE id = ?");
			stmt.setLong(1, transactionDTO.getId());
			ResultSet rs = stmt.executeQuery();
			return rs.next();
		} catch (SQLException ex) {
			throw new RuntimeException("Error querying order in shard", ex);
		}
	}
}