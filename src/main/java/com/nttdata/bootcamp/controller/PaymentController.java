//package com.nttdata.bootcamp.controller;
//
//import com.nttdata.bootcamp.entity.Payment;
//import com.nttdata.bootcamp.entity.dto.PaymentDto;
//import com.nttdata.bootcamp.service.PaymentService;
//import com.nttdata.bootcamp.util.Constant;
//import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import javax.validation.Valid;
//import java.util.Date;
//
//@CrossOrigin(origins = "*")
//@RestController
//@RequestMapping("/payment")
//public class PaymentController {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);
//
//    @Autowired
//    private PaymentService paymentService;
//
//    // ===============================
//    //   GET ALL PAYMENTS
//    // ===============================
//    @GetMapping("/findAllPayments")
//    public Flux<Payment> findAllPayments() {
//        return paymentService.findAll()
//                .doOnSubscribe(s -> LOGGER.info("Searching all payments"))
//                .doOnNext(p -> LOGGER.info("Payment: {}", p));
//    }
//
//    // ===============================
//    //   GET PAYMENTS BY ACCOUNT
//    // ===============================
//    @GetMapping("/findAllPaymentsByAccountNumber/{accountNumber}")
//    public Flux<Payment> findAllPaymentsByAccountNumber(@PathVariable String accountNumber) {
//
//        return paymentService.findByAccountNumber(accountNumber)
//                .doOnSubscribe(s -> LOGGER.info("Searching payments for account {}", accountNumber))
//                .doOnNext(p -> LOGGER.info("Payment found: {}", p));
//    }
//
//    // ===============================
//    //   FIND BY NUMBER
//    // ===============================
//    @CircuitBreaker(name = "payments", fallbackMethod = "fallBackPayment")
//    @GetMapping("/findByPaymentsNumber/{numberPayment}")
//    public Mono<Payment> findByPaymentNumber(@PathVariable String numberPayment) {
//        LOGGER.info("Searching payment by number {}", numberPayment);
//        return paymentService.findByNumber(numberPayment);
//    }
//
//    // ===============================
//    //   SAVE PAYMENT
//    // ===============================
//    @CircuitBreaker(name = "payments", fallbackMethod = "fallBackPayment")
//    @PostMapping("/savePayment")
//    public Mono<Payment> savePayment(@Valid @RequestBody PaymentDto dto) {
//
//        Payment payment = new Payment();
//        payment.setDni(dto.getDni());
//        payment.setAccountNumber(dto.getAccountNumber());
//        payment.setPaymentNumber(dto.getPaymentNumber());
//        payment.setTypeAccount(Constant.TYPE_ACCOUNT);
//        payment.setAmount(dto.getAmount());
//        payment.setCommission(0.00);
//        payment.setCreationDate(new Date());
//        payment.setModificationDate(new Date());
//
//        LOGGER.info("Saving payment: {}", payment);
//
//        return paymentService.savePayment(payment)
//                .doOnSuccess(p -> LOGGER.info("Payment saved: {}", p));
//    }
//
//    // ===============================
//    //   UPDATE PAYMENT
//    // ===============================
//    @CircuitBreaker(name = "payments", fallbackMethod = "fallBackPayment")
//    @PutMapping("/updatePayment/{numberTransaction}")
//    public Mono<Payment> updatePayment(
//            @PathVariable String numberTransaction,
//            @Valid @RequestBody Payment dataPayment) {
//
//        dataPayment.setPaymentNumber(numberTransaction);
//        dataPayment.setModificationDate(new Date());
//
//        LOGGER.info("Updating payment: {}", dataPayment);
//
//        return paymentService.updatePayment(dataPayment)
//                .doOnSuccess(p -> LOGGER.info("Payment updated: {}", p));
//    }
//
//    // ===============================
//    //   DELETE PAYMENT
//    // ===============================
//    @CircuitBreaker(name = "payments", fallbackMethod = "fallBackVoid")
//    @DeleteMapping("/deletePayment/{numberTransaction}")
//    public Mono<Void> deletePayment(@PathVariable String numberTransaction) {
//        LOGGER.info("Deleting payment {}", numberTransaction);
//        return paymentService.deletePayment(numberTransaction)
//                .doOnSuccess(v -> LOGGER.info("Payment deleted: {}", numberTransaction));
//    }
//
//    // ===============================
//    //   COUNT PAYMENTS
//    // ===============================
//    @GetMapping("/getCountPayments/{accountNumber}")
//    public Mono<Long> getCountPayments(@PathVariable String accountNumber) {
//        return paymentService.findByAccountNumber(accountNumber)
//                .count()
//                .doOnSuccess(count -> LOGGER.info("Payments count for {} = {}", accountNumber, count));
//    }
//
//    // ===============================
//    //   FALLBACKS
//    // ===============================
////    private Mono<Payment> fallBackPayment(String param, Exception e) {
////        LOGGER.error("Fallback triggered for param: {}, error: {}", param, e.getMessage());
////        return Mono.just(new Payment());
////    }
//    //ok si graba
////    private Mono<Payment> fallBackPayment(PaymentDto dto, Throwable ex) {
////
////        Payment payment = new Payment();
////        payment.setPaymentNumber("0");
////        payment.setAccountNumber(dto.getAccountNumber());
////        payment.setDni(dto.getDni());
////        payment.setAmount(dto.getAmount());
////
////        return Mono.just(payment);
////    }
//
//    //SAVE and UPDATE
//    private Mono<Payment> fallBackPayment(String numberTransaction, Payment dataPayment, Throwable ex) {
//
//        Payment fallback = new Payment();
//        fallback.setPaymentNumber(numberTransaction);
//        fallback.setAccountNumber(dataPayment.getAccountNumber());
//        fallback.setDni(dataPayment.getDni());
//        fallback.setAmount(dataPayment.getAmount());
//        fallback.setCreationDate(new Date());
//        fallback.setModificationDate(new Date());
//
//        return Mono.just(fallback);
//    }
//
//    private Mono<Payment> fallBackPayment(String numberPayment, Throwable ex) {
//        LOGGER.error("Fallback FIND triggered for {}, error={}", numberPayment, ex.toString());
//        Payment p = new Payment();
//        p.setPaymentNumber(numberPayment);
//        return Mono.just(p);
//    }
//
//
//
//    private Mono<Void> fallBackVoid(String param, Exception e) {
//        LOGGER.error("Fallback VOID triggered for param: {}, error: {}", param, e.getMessage());
//        return Mono.empty();
//    }
//
//}
package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Payment;
import com.nttdata.bootcamp.entity.dto.PaymentDto;
import com.nttdata.bootcamp.service.PaymentService;
import com.nttdata.bootcamp.util.Constant;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Date;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/payment")
public class PaymentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    // ===============================
    //   GET ALL PAYMENTS
    // ===============================
    @GetMapping("/findAllPayments")
    public Flux<Payment> findAllPayments() {
        return paymentService.findAll()
                .doOnSubscribe(s -> LOGGER.info("Searching all payments"))
                .doOnNext(p -> LOGGER.info("Payment: {}", p));
    }

    // ===============================
    //   GET PAYMENTS BY ACCOUNT
    // ===============================
    @GetMapping("/findAllPaymentsByAccountNumber/{accountNumber}")
    public Flux<Payment> findAllPaymentsByAccountNumber(@PathVariable String accountNumber) {

        return paymentService.findByAccountNumber(accountNumber)
                .doOnSubscribe(s ->
                        LOGGER.info("Searching payments for account {}", accountNumber))
                .doOnNext(p -> LOGGER.info("Payment found: {}", p));
    }

    // ===============================
    //   FIND BY NUMBER
    // ===============================
    @CircuitBreaker(name = "payments", fallbackMethod = "fallBackPayment")
    @GetMapping("/findByPaymentsNumber/{numberPayment}")
    public Mono<Payment> findByPaymentNumber(@PathVariable String numberPayment) {
        LOGGER.info("Searching payment by number {}", numberPayment);
        return paymentService.findByNumber(numberPayment);
    }

    // ===============================
    //   SAVE PAYMENT
    // ===============================
    @CircuitBreaker(name = "payments", fallbackMethod = "fallBackPayment")
    @PostMapping("/savePayment")
    public Mono<Payment> savePayment(@Valid @RequestBody PaymentDto dto) {

        Payment payment = new Payment();
        payment.setDni(dto.getDni());
        payment.setAccountNumber(dto.getAccountNumber());
        payment.setPaymentNumber(dto.getPaymentNumber());
        payment.setTypeAccount(Constant.TYPE_ACCOUNT);
        payment.setAmount(dto.getAmount());
        payment.setCommission(0.00);
        payment.setCreationDate(new Date());
        payment.setModificationDate(new Date());
        LOGGER.info("Saving payment: {}", payment);
        return paymentService.savePayment(payment)
                .doOnSuccess(p -> LOGGER.info("Payment saved: {}", p));
    }

    // ===============================
    //   UPDATE PAYMENT
    // ===============================
    @CircuitBreaker(name = "payments", fallbackMethod = "fallBackPayment")
    @PutMapping("/updatePayment/{numberTransaction}")
    public Mono<Payment> updatePayment(
            @PathVariable String numberTransaction,
            @Valid @RequestBody Payment dataPayment) {

        dataPayment.setPaymentNumber(numberTransaction);
        dataPayment.setModificationDate(new Date());
        LOGGER.info("Updating payment: {}", dataPayment);

        return paymentService.updatePayment(dataPayment)
                .doOnSuccess(p -> LOGGER.info("Payment updated: {}", p));
    }

    // ===============================
    //   DELETE PAYMENT
    // ===============================
    @CircuitBreaker(name = "payments", fallbackMethod = "fallBackVoid")
    @DeleteMapping("/deletePayment/{numberTransaction}")
    public Mono<Void> deletePayment(@PathVariable String numberTransaction) {
        LOGGER.info("Deleting payment {}", numberTransaction);
        return paymentService.deletePayment(numberTransaction)
                .doOnSuccess(v -> LOGGER.info("Payment deleted: {}", numberTransaction));
    }

    // ===============================
    //   FALLBACKS
    // ===============================

    // FALLBACK PARA FIND BY NUMBER
    private Mono<Payment> fallBackPayment(String numberPayment, Throwable ex) {
        LOGGER.error("Fallback FIND triggered for {}, error={}", numberPayment, ex.toString());

        Payment fallback = new Payment();
        fallback.setPaymentNumber(numberPayment);
        fallback.setDni("N/A");
        fallback.setAccountNumber("N/A");
        fallback.setAmount(0.0);

        return Mono.just(fallback);
    }

    // FALLBACK PARA SAVE PAYMENT
    private Mono<Payment> fallBackPayment(PaymentDto dto, Throwable ex) {
        LOGGER.error("Fallback SAVE triggered, dto={}, error={}", dto, ex.toString());

        Payment fallback = new Payment();
        fallback.setPaymentNumber("0");
        fallback.setAccountNumber(dto.getAccountNumber());
        fallback.setDni(dto.getDni());
        fallback.setAmount(dto.getAmount());
        fallback.setCreationDate(new Date());
        fallback.setModificationDate(new Date());

        return Mono.just(fallback);
    }

    // FALLBACK PARA UPDATE PAYMENT
    private Mono<Payment> fallBackPayment(String numberTransaction, Payment dataPayment, Throwable ex) {
        LOGGER.error("Fallback UPDATE triggered for {}, error={}", numberTransaction, ex.toString());

        Payment fallback = new Payment();
        fallback.setPaymentNumber(numberTransaction);
        fallback.setAccountNumber(dataPayment.getAccountNumber());
        fallback.setDni(dataPayment.getDni());
        fallback.setAmount(dataPayment.getAmount());
        fallback.setCreationDate(new Date());
        fallback.setModificationDate(new Date());

        return Mono.just(fallback);
    }

    // FALLBACK PARA DELETE
    private Mono<Void> fallBackVoid(String param, Throwable e) {
        LOGGER.error("Fallback VOID triggered for param: {}, error: {}", param, e.getMessage());
        return Mono.empty();
    }
}
