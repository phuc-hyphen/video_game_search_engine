package fr.lernejo.search.api;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;

import static org.junit.jupiter.api.Assertions.*;

class AmqpConfigurationTest {

    AmqpConfiguration amp1 = new AmqpConfiguration();

    @Test
    void check_queue_info() {
        assertEquals(new Queue("game_infor", true), amp1.queue(),
            "fail to add new queue");
    }

}
