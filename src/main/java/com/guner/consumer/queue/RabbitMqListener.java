package com.guner.consumer.queue;

import com.guner.consumer.entity.ChargingRecord;
import com.guner.consumer.exception.MessageNotSuitableException;
import com.guner.consumer.service.ChargingRecordService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMqListener {

    private final ChargingRecordService chargingRecordService;


    // NACK with throwing Exception
    //@RabbitListener(queues = "${single-consumer.queue.name.single-queue}", containerFactory = "rabbitListenerContainerFactory")
    @RabbitListener(queues = "${single-consumer.queue.name.single-queue}")
    public void listenMessage(ChargingRecord chargingRecord) {
        log.debug("Charging Message Received, thread: {}", Thread.currentThread().getName());
        if (chargingRecord.getSourceGsm().endsWith("0")) {
            log.error("Charging Message Source Gsm ends with 0, NACK with throwing Exception");
            throw new MessageNotSuitableException("Source GSM ends with 0");
        }
        chargingRecordService.createChargingRecord(chargingRecord);
    }


    /*
    @RabbitListener(queues = "${single-consumer.queue.name.single-queue}", ackMode = "MANUAL")
    public void listenWithSpringMessage(org.springframework.messaging.Message<ChargingRecord> messageChargingRecord,
                                             Channel channel,
                                             @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.debug("Charging Message Received, thread: {}", Thread.currentThread().getName());
        if (messageChargingRecord.getPayload().getSourceGsm().endsWith("0")) {
            log.error("Charging Message Source Gsm ends with 0, NACK");
            try {
                //channel.basicNack(deliveryTag, false, true); // requeue
                channel.basicNack(deliveryTag, false, false); // no requeue
            } catch (Exception e) {
                log.error("Error while acknowledging message", e);
            }
        } else {
            chargingRecordService.createChargingRecord(messageChargingRecord.getPayload());
            try {
                channel.basicAck(deliveryTag, false);
            } catch (Exception e) {
                log.error("Error while acknowledging message", e);
            }
        }
    }

     */

}
