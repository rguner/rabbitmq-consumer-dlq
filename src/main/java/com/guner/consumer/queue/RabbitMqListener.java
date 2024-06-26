package com.guner.consumer.queue;

import com.guner.consumer.entity.ChargingRecord;
import com.guner.consumer.exception.MessageNotSuitableException;
import com.guner.consumer.service.ChargingRecordService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMqListener {

    private final RabbitTemplate rabbitTemplate;
    private final ChargingRecordService chargingRecordService;

    @Value("${single-consumer.max-delivery-count:10}")
    private int maxDeliveryCount;
    private static final String COUNT_FIELD = "count";

    @Value("${single-consumer.exchange}-exhausted")
    private String exhaustedExchange;


    // NACK with throwing Exception, it works if MessageNotSuitableException messages goes to DLQ, but not goes to exhausted, since there is no code for exhausted
    //@RabbitListener(queues = "${single-consumer.queue}", containerFactory = "rabbitListenerContainerFactory")
    /*
    @RabbitListener(queues = "${single-consumer.queue}")
    public void listenMessage(ChargingRecord chargingRecord) {
        log.debug("Charging Message Received, thread: {}", Thread.currentThread().getName());
        if (chargingRecord.getSourceGsm().endsWith("0")) {
            log.error("Charging Message Source Gsm ends with 0, NACK with throwing Exception");
            throw new MessageNotSuitableException("Source GSM ends with 0");
        }
        chargingRecordService.createChargingRecord(chargingRecord);
    }
     */
}
