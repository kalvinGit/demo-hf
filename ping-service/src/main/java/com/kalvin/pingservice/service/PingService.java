package com.kalvin.pingservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class PingService {

    private final static Logger log = LoggerFactory.getLogger(PingService.class);

    private final WebClient webClient = WebClient.create();

    @Value(value = "${server.port}")
    private int port;

    @Scheduled(fixedRate = 1000)
    public void sendPing() {
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:8080/pong")
                .queryParam("say", port + " > Hello!")
                .build()
                .toUri();
        webClient.get()
                .uri(uri)
                .exchangeToMono(response -> response.bodyToMono(String.class)).subscribe(log::info);
                /*.toEntity(String.class)
                .doOnSuccess(response -> log.info("请求已发送 & Pong响应：{}", response.getBody()))
                .doOnError((throwable) -> log.info("请求发送 & Pong限制了它：{}", throwable.getMessage()))
                .subscribe();*/

    }
}
