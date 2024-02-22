package com.guner.consumer.repository;

import com.guner.consumer.entity.ChargingRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ChargingRecordRepository extends CrudRepository<ChargingRecord, Long> {

    @Query("select c from ChargingRecord c where sourceGsm= :sourceGsm and transactionDate >= :nDaysAgoDate")
    List<ChargingRecord> findChargingRecordsSearchBySourceGsm(@Param("sourceGsm") String sourceGsm, @Param("nDaysAgoDate") Date nDaysAgoDate);
    @Query("select count(c) from ChargingRecord c where sourceGsm= :sourceGsm and transactionDate >= :nDaysAgoDate")
    int findCountOfChargingRecordsSearchBySourceGsm(@Param("sourceGsm") String sourceGsm, @Param("nDaysAgoDate") Date nDaysAgoDate);
}