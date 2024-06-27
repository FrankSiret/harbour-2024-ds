package com.hs.distributedsystem.services.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OutboxDTO implements Serializable {

    private static final long serialVersionUID = -91568699150L;

    private UUID id;

	private TransactionDTO message;

    private String status;

    private Long latency;

    private Instant createdAt;

}
