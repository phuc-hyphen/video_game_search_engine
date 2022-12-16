package fr.lernejo.search.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class AmqpConfigurationTest {
    private static String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    @Test
    void check_queue_exist() {
        RuntimeException test = null;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create("http://localhost:15672/#/queues/%2F/game_info")).header("Authorization", getBasicAuthenticationHeader("guest", "guest")).build();
        try {
            HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
            Assertions.assertThat(response.statusCode()).isEqualTo(200);
        } catch (IOException | InterruptedException e) {
            test = (RuntimeException) e;
        }
        Assertions.assertThat(test).isNull();
    }

}
