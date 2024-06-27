package com.hs.distributedsystem.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hs.distributedsystem.domain.Outbox;
import com.hs.distributedsystem.repository.OutboxRepository;
import com.hs.distributedsystem.services.dto.OutboxDTO;
import com.hs.distributedsystem.services.mapper.OutboxMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class OutboxService {

    private final OutboxMapper outboxMapper;

    private final OutboxRepository outboxRepository;

    OutboxService(
            OutboxMapper outboxMapper,
            OutboxRepository outboxRepository) {
        this.outboxMapper = outboxMapper;
        this.outboxRepository = outboxRepository;
    }

    public OutboxDTO save(OutboxDTO outboxDTO) {
        log.info("Request to save outbox : {}", outboxDTO);
        Outbox outbox = outboxMapper.toEntity(outboxDTO);
        outbox = outboxRepository.save(outbox);
        return outboxMapper.toDto(outbox);
    }

    public Optional<OutboxDTO> findById(UUID uuid) {
        log.info("Request to get outbox by id : {}", uuid);
        return outboxRepository.findById(uuid).map(outboxMapper::toDto);
    }
}
