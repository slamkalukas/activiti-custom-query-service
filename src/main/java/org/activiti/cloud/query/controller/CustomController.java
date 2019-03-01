package org.activiti.cloud.query.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomController {

    @RequestMapping("/abpm")
    public String index() {
        return "Greetings from Spring Boot!";
    }

}
