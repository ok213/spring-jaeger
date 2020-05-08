package com.example.serviceb.services;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.contrib.spring.web.client.HttpHeadersCarrier;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class DelayService {

    private final Tracer tracer;
    private final RestTemplate restTemplate;
    private final String URL = "http://localhost:8082/delay";

    @Autowired
    public DelayService(Tracer tracer) {
        this.tracer = tracer;
        this.restTemplate = new RestTemplate();
    }

    public void delay() {

        Span span = tracer.buildSpan("DelayService: delay")
                .asChildOf(tracer.activeSpan())
                .start();

        // Inter-Process Context Propagation
        HttpHeaders httpHeaders = new HttpHeaders();
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_CLIENT);
        Tags.HTTP_METHOD.set(span, "GET");
        Tags.HTTP_URL.set(span, URL);
        tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new HttpHeadersCarrier(httpHeaders));

        HttpEntity request = new HttpEntity(httpHeaders);

        String message = "";
        try {
            ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, request, String.class);
            message = response.getBody();
        } catch (RestClientException e) {
            message = e.getMessage();
            Tags.ERROR.set(span, true);
        }

        span.log(message).finish();
    }
}
