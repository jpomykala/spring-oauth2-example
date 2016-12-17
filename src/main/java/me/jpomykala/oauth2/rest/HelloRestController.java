package me.jpomykala.oauth2.rest;

import me.jpomykala.oauth2.model.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Evelan on 16/12/2016.
 */
@RestController
public class HelloRestController {

    @RequestMapping("/hello")
    public Map<String, String> recognizeUser(@AuthenticationPrincipal User user) {

        Map<String, String> output = new HashMap<>();
        output.put("hello", "world");
        output.put("firstName", user.getFirstName());
        output.put("lastName", user.getLastName());
        return output;
    }
}
