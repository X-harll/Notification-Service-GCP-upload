//package com.bej03.notis.controller;
//
//import com.bej03.notis.service.MessageProducer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class RabbitMQController {
//
//    @Autowired
//    private MessageProducer producer;
//
//    @GetMapping("/send")
//    public String sendMessage(@RequestParam String message) {
//        producer.sendMessage(message);
//        return "Message sent to RabbitMQ: " + message;
//    }
//}
//
