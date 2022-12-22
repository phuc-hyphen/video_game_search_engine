package fr.lernejo.search.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class AmqpConfigurationTest {
//    private static String getBasicAuthenticationHeader(String username, String password) {
//        String valueToEncode = username + ":" + password;
//        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
//    }
//
//    @Test
//    void check_queue_exist() {
//        IOException test = null;
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create("http://localhost:15672/#/queues/%2F/game_info")).header("Authorization", getBasicAuthenticationHeader("guest", "guest")).build();
//        try {
//            HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
//            Assertions.assertThat(response.statusCode()).isEqualTo(200);
//        } catch (IOException | InterruptedException e) {
//            test = (IOException) e;
//        }
//        Assertions.assertThat(test).isNull();
//    }

}
