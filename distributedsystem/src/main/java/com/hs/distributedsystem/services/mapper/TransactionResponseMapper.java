package com.hs.distributedsystem.services.mapper;

import org.mapstruct.Mapper;

import com.hs.distributedsystem.domain.TransactionResponse;
import com.hs.distributedsystem.services.dto.TransactionResponseDTO;

@Mapper(componentModel = "spring" )
public interface TransactionResponseMapper extends EntityMapper<TransactionResponseDTO, TransactionResponse> {
}