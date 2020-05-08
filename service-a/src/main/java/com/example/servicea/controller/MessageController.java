package com.example.servicea.controller;

import com.example.servicea.dto.MessageDto;
import com.example.servicea.service.DelayService;
import com.example.servicea.service.MessageService;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/sendMessage")
public class MessageController {

    private final Tracer tracer;
    private final DelayService delayService;
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<String> sendMessage(@RequestBody MessageDto messageDto) {

        Span span = tracer.activeSpan();
        span.setOperationName("MessageController: sendMessage");

        String recipientEmail = messageDto.getRecipientEmail();
        Long recipientId = messageDto.getRecipientId();
        String result = "";

        delayService.delay();
        if ((recipientEmail != null && recipientId != null)
                || (recipientEmail == null && recipientId != null)) {
            result = messageService.sendMessageToPersonById(messageDto);
        } else if (recipientId == null && recipientEmail != null) {
            result = messageService.sendMessageToPersonByEmail(messageDto);
        } else {
            result = "recipient not found!";
            Tags.ERROR.set(span, true);
        }

        span.log(result).finish();
        return ResponseEntity.ok(result);
    }

}
