package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Payment;
import com.nttdata.bootcamp.entity.enums.EventType;
import com.nttdata.bootcamp.repository.PaymentRepository;
import com.nttdata.bootcamp.service.KafkaService;
import com.nttdata.bootcamp.service.PaymentService;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaService kafkaService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              KafkaService kafkaService) {
        this.paymentRepository = paymentRepository;
        this.kafkaService = kafkaService;
    }

    @Override
    public Flux<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public Flux<Payment> findByAccountNumber(String accountNumber) {
        return paymentRepository.findAll()
                .filter(p -> p.getAccountNumber().equals(accountNumber));
    }

    @Override
    public Mono<Payment> findByNumber(String number) {
        return paymentRepository.findAll()
                .filter(p -> p.getPaymentNumber().equals(number))
                .next();
    }

    @Override
    public Mono<Payment> savePayment(Payment dataPayment) {

        return findByNumber(dataPayment.getPaymentNumber())
                .flatMap(existing ->
                        Mono.<Payment>error(new RuntimeException(
                                "This payment number " +
                                        dataPayment.getPaymentNumber() + " exists"
                        ))
                )
                .switchIfEmpty(
                        paymentRepository.save(dataPayment)
                                .flatMap(saved ->
                                        kafkaService.publish(saved, EventType.CREATED)
                                                .thenReturn(saved)
                                )
                );

    }

    @Override
    public Mono<Payment> updatePayment(Payment dataPayment) {

        return findByNumber(dataPayment.getPaymentNumber())
                .switchIfEmpty(
                        Mono.error(new RuntimeException(
                                "The payment " + dataPayment.getPaymentNumber() + " does not exist"))
                )
                .flatMap(existing -> {
                    dataPayment.setCreationDate(existing.getCreationDate());
                    dataPayment.setDni(existing.getDni());
                    dataPayment.setAmount(existing.getAmount());
                    dataPayment.setModificationDate(new Date());

                    return paymentRepository.save(dataPayment)
                            .flatMap(updated ->
                                    kafkaService.publish(updated, EventType.UPDATED)
                                            .thenReturn(updated)
                            );
                });
    }


    @Override
    public Mono<Void> deletePayment(String number) {

        return findByNumber(number)
                .switchIfEmpty(
                        Mono.error(new Error("This payment number " + number + " does not exist"))
                )
                .flatMap(paymentRepository::delete);
    }

}
