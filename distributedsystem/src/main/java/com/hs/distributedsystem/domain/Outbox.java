package com.hs.distributedsystem.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Entity
@Table(name = "outbox")
public class Outbox implements Serializable {

    private static final long serialVersionUID = -9156869915L;

    @Id
    private UUID id;

    @ToString.Exclude
	@OneToOne(optional = false, cascade = CascadeType.ALL)
	private Transaction message;

    @Column(name = "status")
    private String status;

    @Column(name = "latency")
    private Long latency;

    @Column(name = "created_at")
    private Instant createdAt;

}
