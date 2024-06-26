package com.hs.distributedsystem.domain;

import jakarta.persistence.*;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Entity
@Table(name = "transaction_response")
public class TransactionResponse {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "sequence_generator")
	@Column(name = "id")
	private Long id;

	@Column(name = "transaction_id")
	private String transactionId;

	@Column(name = "status")
	private String status;

	@ToString.Exclude
	@OneToOne(optional = false, cascade = CascadeType.ALL)
	private Transaction detail;

}
