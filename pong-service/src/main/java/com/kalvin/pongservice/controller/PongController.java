package com.kalvin.pongservice.controller;

import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class PongController {

    private final Logger log = LoggerFactory.getLogger(PongController.class);

    private final RateLimiter rateLimiter = RateLimiter.create(2.0);

    private final AtomicLong epochMilli = new AtomicLong(0);

    @GetMapping("/pong")
    public Mono<ResponseEntity<String>> pong(String say) {
        return Mono.defer(() -> handle(say));
    }

    private Mono<ResponseEntity<String>> handle(String say) {
        if (rateLimiter.tryAcquire()) {
            try (FileChannel channel = FileChannel.open(Paths.get("rate_limit.lock"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                 FileLock lock = channel.tryLock();) {
                if (Objects.nonNull(lock)) {
                    log.info("接收到ping请求：{}", say);

                    // 确保单位秒内只允许一个请求返回：世界
                    Instant start = Instant.now();
                    if (start.toEpochMilli() > epochMilli.get()) {
                        Instant end = start.plusSeconds(1);
                        epochMilli.set(end.toEpochMilli() - 150);
                        return Mono.just(new ResponseEntity<>("世界", HttpStatus.OK));
                    } else {
                        return Mono.just(new ResponseEntity<>("请求发送 & Pong 限制了它", HttpStatus.TOO_MANY_REQUESTS));
                    }
                } else {
                    return Mono.just(new ResponseEntity<>("请求发送 & Pong 限制了它", HttpStatus.TOO_MANY_REQUESTS));
                }
            } catch (Exception e) {
                return Mono.just(new ResponseEntity<>("请求发送 & Pong 限制了它", HttpStatus.TOO_MANY_REQUESTS));
            }
        } else {
            return Mono.just(new ResponseEntity<>("受速率限制，此次请求已跳过", HttpStatus.SERVICE_UNAVAILABLE));
        }
    }

}
