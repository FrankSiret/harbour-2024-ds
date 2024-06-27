package com.hs.distributedsystem.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hs.distributedsystem.domain.Outbox;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, UUID> {

}
