package com.ex.tjspring.common.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    private int counter = 0;
    @GetMapping("/")
    public String get() {
        counter++;
        return "counter : " + counter + " , currentTimeMillis : " + String.valueOf( System.currentTimeMillis() );
    }
}
