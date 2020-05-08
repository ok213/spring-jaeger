package com.example.servicea.service;

import com.example.servicea.config.HttpHeadersCarrier;
import com.example.servicea.dto.MessageDto;
import com.example.servicea.dto.PersonDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final Tracer tracer;
    private final RestTemplate restTemplate;
    private String URL = "http://localhost:8081/person/";

    @Autowired
    public MessageServiceImpl(Tracer tracer) {
        this.tracer = tracer;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public String sendMessageToPersonById(MessageDto messageDto) {

        Span span = tracer.buildSpan("MessageService: sendMessageToPersonById")
                .asChildOf(tracer.activeSpan())
                .start();

        // Inter-Process Context Propagation
        HttpHeaders httpHeaders = new HttpHeaders();
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_CLIENT);
        Tags.HTTP_METHOD.set(span, "GET");
        Tags.HTTP_URL.set(span, URL + messageDto.getRecipientId());
        span.setBaggageItem("A-BAGGAGE", "TEST BAGGAGE FROM A-SERVICE");
        tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new HttpHeadersCarrier(httpHeaders));

        String result = "";

        // не показывает трассу к сервису В! нужно передавать заголовки в запросе к сервису В
//        PersonDto person = restTemplate.getForObject(URL + messageDto.getRecipientId(), PersonDto.class);
//        result = person == null ? "RECIPIENT NOT FOUND!" : "MESSAGE: <" + messageDto.getText() + "> for <" + person.getEmail() + "> SENT!";

        HttpEntity request = new HttpEntity(httpHeaders);
        PersonDto person;
        try {
            ResponseEntity<String> response = restTemplate.exchange(URL + messageDto.getRecipientId(), HttpMethod.GET, request, String.class);
            person = new ObjectMapper().readValue(response.getBody(), PersonDto.class);
            result = "MESSAGE: <" + messageDto.getText() + "> for <" + person.getEmail() + "> SENT!";
        } catch (RestClientException | JsonProcessingException ex) {
            result = "RECIPIENT NOT FOUND!";
            Tags.ERROR.set(span, true);
        }

        span.log(result).finish();
        return result;
    }

    @Override
    public String sendMessageToPersonByEmail(MessageDto messageDto) {
        Span span = tracer.buildSpan("MessageService: sendMessageToPersonByEmail")
                .asChildOf(tracer.activeSpan())
                .start();

        // Inter-Process Context Propagation
        HttpHeaders httpHeaders = new HttpHeaders();
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_CLIENT);
        Tags.HTTP_METHOD.set(span, "GET");
        Tags.HTTP_URL.set(span, URL);
        tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new HttpHeadersCarrier(httpHeaders));

        String result = "RECIPIENT NOT FOUND!";

        HttpEntity request = new HttpEntity(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, request, String.class);
        PersonDto[] persons;
        try {
            persons = new ObjectMapper().readValue(response.getBody(), PersonDto[].class);
            for (int i = 0; i < persons.length; i++) {
                if(persons[i].getEmail().equals(messageDto.getRecipientEmail())) {
                    result = "MESSAGE: <"
                            + messageDto.getText()
                            + "> for <"
                            + persons[i].getFirstName() + " " + persons[i].getLastName()
                            + "> SENT!";
                    break;
                }
            }
        } catch (RestClientException | JsonProcessingException e) {
            result = "RECIPIENT NOT FOUND!";
            Tags.ERROR.set(span, true);
        }

        span.log(result).finish();
        return result;
    }

}
