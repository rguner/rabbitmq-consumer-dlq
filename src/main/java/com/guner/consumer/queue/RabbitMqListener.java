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


    @RabbitListener(queues = "${single-consumer.queue}", ackMode = "MANUAL")
    public void listenWithSpringMessage(org.springframework.messaging.Message<ChargingRecord> messageChargingRecord,
                                             Channel channel,
                                             @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                             @Header(name = "x-death",required = false) List<Map<String, ?>> xdeathHeader) {
        log.debug("Charging Message Received, thread: {}", Thread.currentThread().getName());
        int deliveryCount = getDeliveryCount(xdeathHeader);
        if (deliveryCount >= maxDeliveryCount) {
            log.debug("Message is exhausted with delivery count [{}]", deliveryCount);
            rabbitTemplate.convertAndSend(exhaustedExchange, ".#", messageChargingRecord.getPayload());
            try {
                // basicAck(long deliveryTag, boolean multiple)
                channel.basicAck(deliveryTag, false);
            } catch (Exception e) {
                log.error("Error while acknowledging message", e);
            }
        } else {
            processMessage(messageChargingRecord, channel, deliveryTag);
        }
    }

    private void processMessage(Message<ChargingRecord> messageChargingRecord, Channel channel, long deliveryTag) {
        if (messageChargingRecord.getPayload().getSourceGsm().endsWith("0")) {
            log.error("Charging Message Source Gsm ends with 0, NACK");
            try {
                //basicNack(long deliveryTag, boolean multiple, boolean requeue)
                //channel.basicNack(deliveryTag, false, true); // requeue, it requeues to the same queue. Not to DLQ
                channel.basicNack(deliveryTag, false, false); // no requeue, it sends to DLQ
            } catch (Exception e) {
                log.error("Error while negative acknowledging message", e);
            }
        } else {
            chargingRecordService.createChargingRecord(messageChargingRecord.getPayload());
            try {
                // basicAck(long deliveryTag, boolean multiple)
                channel.basicAck(deliveryTag, false);
            } catch (Exception e) {
                log.error("Error while acknowledging message", e);
            }
        }
    }

    public int getDeliveryCount(List<Map<String, ?>> deathHeader) {
        return (int) (CollectionUtils.isEmpty(deathHeader) ? 0
                : (long) deathHeader.get(0).get(COUNT_FIELD));
    }

}
