package com.facts.financial_facts_service.components;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Optional;


@NoArgsConstructor
@Component
public class WebClientFactory {

    static Logger logger = LoggerFactory.getLogger(WebClientFactory.class);

    public WebClient buildWebClient(String url, Optional<Map<String, String>> headersOptional) {
        logger.info("Initializing webclient for url {}...", url);
        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        return WebClient.builder()
            .exchangeStrategies(strategies)
            .baseUrl(url)
            .defaultHeaders(consumer -> {
                consumer.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                if (headersOptional.isPresent()) {
                    Map<String, String> headers = headersOptional.get();
                    headers.keySet().stream().forEach(key -> {
                        consumer.add(key, headers.get(key));
                    });
                }
            }).build();
    }



}
