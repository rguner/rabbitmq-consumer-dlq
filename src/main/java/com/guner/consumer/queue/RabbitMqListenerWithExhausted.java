package com.guner.consumer.queue;

import com.guner.consumer.entity.ChargingRecord;
import com.guner.consumer.exception.MessageNotSuitableException;
import com.guner.consumer.service.ChargingRecordService;
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
public class RabbitMqListenerWithExhausted {

    private final RabbitTemplate rabbitTemplate;
    private final ChargingRecordService chargingRecordService;

    @Value("${single-consumer.max-delivery-count:10}")
    private int maxDeliveryCount;
    private static final String COUNT_FIELD = "count";

    @Value("${single-consumer.exchange}-exhausted")
    private String exhaustedExchange;

    @RabbitListener(queues = "${single-consumer.queue}")
    public void listenWithSpringMessage(org.springframework.messaging.Message<ChargingRecord> messageChargingRecord,
                                             @Header(name = "x-death",required = false) List<Map<String, ?>> xdeathHeader) {
        log.debug("Charging Message Received, thread: {}", Thread.currentThread().getName());
        int deliveryCount = getDeliveryCount(xdeathHeader);
        if (deliveryCount >= maxDeliveryCount) {
            log.debug("Message is exhausted with delivery count [{}]", deliveryCount);
            rabbitTemplate.convertAndSend(exhaustedExchange, ".#", messageChargingRecord.getPayload());
        } else {
            processMessage(messageChargingRecord);
        }
    }

    private void processMessage(Message<ChargingRecord> messageChargingRecord) {
        if (messageChargingRecord.getPayload().getSourceGsm().endsWith("0")) {
            log.error("Charging Message Source Gsm ends with 0");
            throw new MessageNotSuitableException("Source GSM ends with 0"); // it sends to DLQ
        } else {
            chargingRecordService.createChargingRecord(messageChargingRecord.getPayload());
        }
    }

    public int getDeliveryCount(List<Map<String, ?>> deathHeader) {
        return (int) (CollectionUtils.isEmpty(deathHeader) ? 0
                : (long) deathHeader.get(0).get(COUNT_FIELD));
    }

}
