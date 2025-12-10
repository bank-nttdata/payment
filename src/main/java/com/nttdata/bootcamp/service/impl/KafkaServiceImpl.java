package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Payment;
import com.nttdata.bootcamp.entity.enums.EventType;
import com.nttdata.bootcamp.events.EventKafka;
import com.nttdata.bootcamp.events.PaymentCreatedEventKafka;
import com.nttdata.bootcamp.service.KafkaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.util.Date;
import java.util.UUID;

//@Service
//public class KafkaServiceImpl implements KafkaService {
//
//    private final KafkaSender<String, EventKafka<?>> kafkaSender;
//
//    @Value("${topic.payment.name}")
//    private String topicPayment;
//
//    public KafkaServiceImpl(KafkaSender<String, EventKafka<?>> kafkaSender) {
//        this.kafkaSender = kafkaSender;
//    }
//
//    @Override
//    public Mono<Void> publish(Payment payment) {
//
//        PaymentCreatedEventKafka created = new PaymentCreatedEventKafka();
//        created.setId(UUID.randomUUID().toString());
//        created.setType(EventType.CREATED);
//        created.setDate(new Date());
//        created.setData(payment);
//
//        SenderRecord<String, EventKafka<?>, Void> record =
//                SenderRecord.create(topicPayment, null, null, created.getId(), created, null);
//
//        return kafkaSender.send(Mono.just(record))
//                .map(SenderResult::recordMetadata)
//                .doOnNext(metadata ->
//                        System.out.println("Mensaje enviado a Kafka -> Topic: " +
//                                metadata.topic() +
//                                " | Partition: " + metadata.partition() +
//                                " | Offset: " + metadata.offset())
//                )
//                .doOnError(e ->
//                        System.err.println("ERROR enviando mensaje Kafka: " + e.getMessage())
//                )
//                .then();
//    }
//}

@Service
public class KafkaServiceImpl implements KafkaService {

    private final KafkaSender<String, EventKafka<?>> kafkaSender;

    @Value("${topic.payment.name}")
    private String topicPayment;

    public KafkaServiceImpl(KafkaSender<String, EventKafka<?>> kafkaSender) {
        this.kafkaSender = kafkaSender;
    }

    @Override
    public Mono<Void> publish(Payment payment, EventType eventType) {

        EventKafka<Payment> event = new EventKafka<>();
        event.setId(UUID.randomUUID().toString());
        event.setType(eventType);
        event.setDate(new Date());
        event.setData(payment);

        SenderRecord<String, EventKafka<?>, Void> record =
                SenderRecord.create(
                        topicPayment,
                        null,
                        null,
                        event.getId(),
                        event,
                        null
                );

        return kafkaSender.send(Mono.just(record))
                .map(SenderResult::recordMetadata)
                .doOnNext(meta -> System.out.println(
                        "Evento [" + eventType + "] enviado → " +
                                "Topic: " + meta.topic() +
                                " | Partition: " + meta.partition() +
                                " | Offset: " + meta.offset()
                ))
                .doOnError(e -> System.err.println(
                        "ERROR al enviar evento Kafka: " + e.getMessage()
                ))
                .then();
    }
}
