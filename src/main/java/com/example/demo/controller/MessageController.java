package com.example.demo.controller;

import com.example.demo.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableBinding(Source.class)
public class MessageController {
    @Autowired
    Source source;

    @PostMapping(value="/sendMsg")
    public String sendMessage(@RequestParam("message") String message) {
        Message msg = new Message();
        msg.setMessage(message);

        source.output().send(MessageBuilder.withPayload(msg).setHeader("myheader", "myheaderValue").build());
        System.out.println(message + ": Successfully sent to rabbitmq");
        return "success";
    }

    @GetMapping(value="/getMsg")
    public Message getMessage() {
        Message msg = new Message();
        msg.setMessage("message sending...");

        return msg;
    }
}
