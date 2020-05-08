package com.example.servicedelay;

import io.opentracing.Span;
import io.opentracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/delay")
public class DelayController {

    private final Tracer tracer;
    private final Random random;

    @Autowired
    public DelayController(Tracer tracer) {
        this.tracer = tracer;
        this.random = new Random();
    }

    @GetMapping
    public ResponseEntity<String> delay() {

        Span span = tracer.activeSpan();
        span.setOperationName("DelayController: delay");

        int delay = random.nextInt(4);
        try {
            TimeUnit.SECONDS.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String message = "delay = " + delay + "s";
        span.log(message).finish();
        return ResponseEntity.ok(message);
    }

}
