package com.guner.consumer.controller;

import com.guner.consumer.entity.ChargingRecord;
import com.guner.consumer.service.ChargingRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChargingRecordController {

    private final ChargingRecordService chargingRecordService;

    @GetMapping("/charging-records")
    public List<ChargingRecord> getAllChargingRecords() {
        return chargingRecordService.getAllChargingRecords();
    }

}