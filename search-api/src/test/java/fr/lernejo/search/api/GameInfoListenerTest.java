package fr.lernejo.search.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Base64;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class GameInfoListenerTest {
    String SAMPLE_RESPONSE_PAYLOAD = """
        {
            "id":"1000",
            "title": "my super game",
            "thumbnail": "http://somehost/somepathwithcorsdisabled",
            "short_description": "awesome game",
            "genre": "ftps",
            "platform": "PS1000",
            "publisher": "Machin produces",
            "developer": "Bidule Studios",
            "release_date": "2022-02-12"
        }
        """.stripTrailing();

    private static String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    @Test
    void check_listener() throws Exception {
        final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        final AbstractApplicationContext springContext = new AnnotationConfigApplicationContext(Launcher.class);
        final var rabbitTemplate = springContext.getBean(RabbitTemplate.class);
        mapper.enable(SerializationFeature.INDENT_OUTPUT).setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

        rabbitTemplate.convertSendAndReceive("", "game_info", SAMPLE_RESPONSE_PAYLOAD, m -> {
            m.getMessageProperties().getHeaders().put("game_id", 1000);
            m.getMessageProperties().setContentType("appplication/json");
            return m;
        });
//        {"vhost":"/","name":"amq.default","properties":{"delivery_mode":1,"headers":{"id":"1000"}},"routing_key":"game_info","delivery_mode":"1","payload":"sdfsdfsd","headers":{"id":"1000"},"props":{},"payload_encoding":"string"}
//        HttpRequest getRequest = HttpRequest.newBuilder()
//            .uri(URI.create("http://localhost:9200/games/_doc/1000"))
//            .header("Authorization", getBasicAuthenticationHeader("elastic", "admin"))
//            .build();
//
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest getRequest = HttpRequest.newBuilder()
//            .uri(URI.create("http://localhost:9200/games/_doc/1000"))
//            .header("Authorization", getBasicAuthenticationHeader("elastic", "admin"))
//            .build();
//        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
//        Assertions.assertThat(response.statusCode()).isEqualTo(200);
    }

}
