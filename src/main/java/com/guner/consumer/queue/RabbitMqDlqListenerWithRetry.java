package com.guner.consumer.queue;

import com.guner.consumer.entity.ChargingRecord;
import com.guner.consumer.exception.MessageNotSuitableException;
import com.guner.consumer.service.ChargingRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMqDlqListenerWithRetry {

    private final ChargingRecordService chargingRecordService;
    public static final long MAX_RETRIES_COUNT = 10;
    public static final String HEADER_X_RETRIES_COUNT = "x-retries-count";

    private final RabbitTemplate rabbitTemplate;

    /**
     *
     *
     Dlq contains x-death headers contains information about original message configuration

     The dead-lettering process adds an array to the header of each dead-lettered message named  x-death.
     This array contains an entry for each dead lettering event, identified by a pair of {queue, reason}.

     Each such entry is a table that consists of several fields:

     queue - the name of the queue the message was in before it was dead-lettered,
     reason - see below,
     time - the date and time the message was dead lettered as a 64-bit AMQP format timestamp,
     exchange - the exchange the message was published to (note that this will be a dead letter exchange if the message is dead lettered multiple times),
     routing-keys - the routing keys (including CC keys but excluding BCC ones) the message was published with,
     count - how many times this message was dead-lettered in this queue for this reason, and
     original-expiration (if the message was dead-letterered due to per-message TTL) - the original expiration property of the message.

     The expiration property is removed from the message on dead-lettering in order to prevent it from expiring again in any queues it is  routed to.

     New entries are prepended to the beginning of the x-death array.

     In case x-death already contains an entry with the same queue and dead lettering reason,
     its count field will be incremented and it will be moved to the beginning of the array.

     The reason is a name describing why the message was dead-lettered and is one of the following:

     rejected - the message was rejected with requeue=false,
     expired - the TTL of the message expired; or
     maxlen - the maximum allowed queue length was exceeded.

     Note that the array is sorted most-recent-first, so the most recent dead-lettering will be recorded in the first entry.


     /* NO NEED TO LISTEN DLQ, With ttl defined in deaa-letter-queue, message will be sent to original queue.
     /*
    @RabbitListener(queues = "${single-consumer.queue.name.single-queue-dlq}")
    public void processFailedMessagesRetryHeaders(ChargingRecord chargingRecord, @Header("x-death") Map<String, List<?>> deathHeader) {
        log.info("DLQ ---- Received message from DLQ with x-death header {} ", deathHeader);
        Long retriesCnt = (Long) deathHeader.get("count").get(0);
        if (retriesCnt > MAX_RETRIES_COUNT) {
            log.info("Discarding message");
            return;
        }
        // original queue'ya gonderirsen retry sıfrlanır, bu yontem retry sayısı checki için mantıklı değil.
        log.info("Retrying message for the {} time", retriesCnt);
        rabbitTemplate.convertAndSend(topicExchange, routingKeySingle, chargingRecord);

    }
    */

}

