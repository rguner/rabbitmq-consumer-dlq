package com.guner.consumer.queue;

import com.guner.consumer.entity.ChargingRecord;
import com.guner.consumer.exception.MessageNotSuitableException;
import com.guner.consumer.service.ChargingRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMqDlqListener {

    private final ChargingRecordService chargingRecordService;

    @RabbitListener(queues = "${single-consumer.queue.name.single-queue-dlq}")
    public void listenDlqMessage(ChargingRecord chargingRecord) {
        log.debug("Charging Message Received from DLQ, thread: {}", Thread.currentThread().getName());
        if (chargingRecord.getSourceGsm().endsWith("0")) {
            log.error("Charging Message Source Gsm ends with 0, removing from DLQ");
            //throw new MessageNotSuitableException("Source GSM ends with 0");
        } else {
            chargingRecordService.createChargingRecord(chargingRecord);
        }
    }

}
