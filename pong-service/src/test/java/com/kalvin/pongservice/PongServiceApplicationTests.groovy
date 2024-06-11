package com.kalvin.pongservice

import com.kalvin.pongservice.controller.PongController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
@AutoConfigureMockMvc
class PongServiceApplicationTests extends Specification {

    @Autowired
    PongController pongController

    def "test request pong success"() {
        given:
        def params = "你好"

        when:
        def isSuccess = pongController.pong(params).block().statusCode.is2xxSuccessful()

        then:
        isSuccess
    }

    def "test request pong limit"() {
        given:
        def params = "你好"

        when:
        def results = []
        for (i in 0..<8) {
            results << pongController.pong(params).block().body
        }

        then:
        def expectedResponses = ["你好", "请求发送 & Pong 限制了它", "受速率限制，此次请求已跳过"]
        results.each { result ->
            assert expectedResponses.contains(result)
        }
    }
}
