package com.kalvin.pingservice

import com.kalvin.pingservice.service.PingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
@AutoConfigureMockMvc
class PingServiceApplicationTests extends Specification {

    @Autowired
    PingService pingService;

    def "test sendPing success"() {
        when:
        pingService.sendPing()

        then:
        noExceptionThrown()
    }

}
