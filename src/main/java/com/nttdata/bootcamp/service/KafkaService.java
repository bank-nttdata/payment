package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Payment;
import com.nttdata.bootcamp.entity.enums.EventType;
import reactor.core.publisher.Mono;

public interface KafkaService {
    Mono<Void> publish(Payment payment, EventType eventTyp);
}
