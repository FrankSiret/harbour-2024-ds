package com.hs.distributedsystem.services.mapper;

import org.mapstruct.Mapper;

import com.hs.distributedsystem.domain.Outbox;
import com.hs.distributedsystem.services.dto.OutboxDTO;

@Mapper(componentModel = "spring")
public interface OutboxMapper extends EntityMapper<OutboxDTO, Outbox> {
}