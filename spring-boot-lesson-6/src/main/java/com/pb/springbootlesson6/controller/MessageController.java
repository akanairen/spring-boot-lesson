package com.pb.springbootlesson6.controller;

import com.pb.springbootlesson6.domain.User;
import com.pb.springbootlesson6.sender.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/msg/")
public class MessageController {

    @Autowired
    private Sender sender;

    @PostMapping
    public String send(String msg) {
        sender.send(msg);
        return msg;
    }

    @PostMapping("user")
    public User sendUser(@RequestBody User user) {
        sender.send(user);
        return user;
    }
}
