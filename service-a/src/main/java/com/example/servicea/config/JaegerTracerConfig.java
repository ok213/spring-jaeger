package com.example.servicea.config;

import org.springframework.stereotype.Component;

@Component
public class JaegerTracerConfig {

    private static final int JAEGER_PORT = 6831;
    private static final String JAEGER_HOST = "localhost";
    private static final String JAEGER_SERVICE_NAME = "A-SERVICE";
    private static final double SAMPLING_RATE = 0.5;

//    @Bean
//    @Primary
//    public Tracer jaegerTracer(RemoteReporter remoteReporter) {
//        return new JaegerTracer.Builder(JAEGER_SERVICE_NAME)
//                .withReporter(remoteReporter)
//                .withMetricsFactory(new NoopMetricsFactory()).withSampler(new ProbabilisticSampler(SAMPLING_RATE))
//                .build();
//    }
//
//    @Bean
//    public RemoteReporter remoteReporter() {
//        return new Builder().withSender(new UdpSender(JAEGER_HOST, JAEGER_PORT, 0)).build();
//    }

//    @Bean
//    public Tracer tracer() {
//        Configuration.SamplerConfiguration samplerConfig = Configuration.SamplerConfiguration.fromEnv()
//                .withType(ProbabilisticSampler.TYPE)
//                .withParam(1);
//
//        Configuration.ReporterConfiguration reporterConfig = Configuration.ReporterConfiguration.fromEnv()
//                .withLogSpans(true);
//
//        Configuration config = new Configuration("spring-service")
//                .withSampler(samplerConfig)
//                .withReporter(reporterConfig);
//
//        return config.getTracer();
//    }

}
