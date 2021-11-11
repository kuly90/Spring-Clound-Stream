package com.example.demo.controller;

import com.example.demo.config.MessagingConfig;
import com.example.demo.model.Message;
import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@EnableBinding(Source.class)
public class MessageController {
    @Autowired
    Source source;

    @Autowired
    RabbitTemplate template;

    @PostMapping(value="/sendMsg")
    public String sendMessage(@RequestBody Message message) {
        Message msg = message;

        source.output().send(MessageBuilder.withPayload(msg).setHeader("myheader", "myheaderValue").build());
        System.out.println(message.getMessage() + ": Successfully sent to rabbitmq");
        return "success";
    }

    @GetMapping(value="/getMsg")
    public Message getMessage() {
        Message msg = new Message();
        msg.setMessage("message sending...");

        return msg;
    }

    @PostMapping("/order/{restaurantName}")
    public String bookOrder(@RequestBody Order order, @PathVariable String restaurantName) {
        order.setOrderId(UUID.randomUUID().toString());
        //restaurantservice
        //payment service
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrder(order);
        orderStatus.setStatus("PROCESS");
        orderStatus.setMessage("order placed succesfully in " + restaurantName);
        template.convertAndSend(MessagingConfig.EXCHANGE, MessagingConfig.ROUTING_KEY, orderStatus);
        return "Success !!";
    }

}
