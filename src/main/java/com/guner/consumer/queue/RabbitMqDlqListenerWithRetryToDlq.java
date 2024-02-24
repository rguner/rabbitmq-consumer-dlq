package com.guner.consumer.queue;

import com.guner.consumer.entity.ChargingRecord;
import com.guner.consumer.service.ChargingRecordService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMqDlqListenerWithRetryToDlq {

    private final ChargingRecordService chargingRecordService;
    public static final long MAX_RETRIES_COUNT = 10;
    public static final String HEADER_X_RETRIES_COUNT = "x-retries-count";

    private final RabbitTemplate rabbitTemplate;

    @Value("${single-consumer.topic-exchange.name}")
    private String topicExchange;

    @Value("${single-consumer.routing.key.single-routing}")
    private String routingKeySingle;

    /*
    @RabbitListener(queues = "${single-consumer.queue.name.single-queue-dlq}")
    public void processFailedMessagesRetryHeaders(ChargingRecord chargingRecord,
                                                  Channel channel,
                                                  @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                                  @Header("x-death") Map<String, List<?>> deathHeader) {
        log.info("DLQ ---- Received message from DLQ with x-death header {} ", deathHeader);
        Long retriesCnt = (Long) deathHeader.get("count").get(0);
        if (retriesCnt > MAX_RETRIES_COUNT) {
            log.info("Discarding message");
            return;
        }
        try {
            channel.basicNack(deliveryTag, false, false);
        } catch (Exception e) {
            log.error("Error while acknowledging message", e);
        }
    }
     */
}

