package com.hs.distributedsystem.services.mapper;

import org.mapstruct.Mapper;

import com.hs.distributedsystem.domain.Transaction;
import com.hs.distributedsystem.services.dto.TransactionDTO;

@Mapper(componentModel = "spring")
public interface TransactionMapper extends EntityMapper<TransactionDTO, Transaction> {
}