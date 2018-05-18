package com.pb.springbootlesson1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootApplication
@Controller
public class SpringBootLesson1Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootLesson1Application.class, args);
    }

    @RequestMapping("/say")
    @ResponseBody
    public String say() {
        return "Goodbye World!";
    }

    @RequestMapping("/map")
    @ResponseBody
    public Map<String, Object> map() {
        return new LinkedHashMap<String, Object>() {{
            put("a", 1);
            put("b", 2);
            put("c", 3);
        }};
    }
}
