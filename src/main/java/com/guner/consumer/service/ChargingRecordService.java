package com.guner.consumer.service;

import com.guner.consumer.entity.ChargingRecord;
import com.guner.consumer.repository.ChargingRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChargingRecordService {
    private final ChargingRecordRepository chargingRecordRepository;

    public List<ChargingRecord> getAllChargingRecords() {
        return (List<ChargingRecord>) chargingRecordRepository.findAll();
    }

    public ChargingRecord createChargingRecord(ChargingRecord chargingRecord) {
        ChargingRecord blockedDestination1 = chargingRecordRepository.save(
                ChargingRecord.builder()
                        .sourceGsm(chargingRecord.getSourceGsm())
                        .targetGsm(chargingRecord.getTargetGsm())
                        .transactionDate(chargingRecord.getTransactionDate())
                        .build());
        return blockedDestination1;
    }

}
