package fr.lernejo.search.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.assertj.core.api.Assertions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Base64;


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


    @Test
    void check_existant_of_doc() {
        final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

        final AbstractApplicationContext springContext = new AnnotationConfigApplicationContext(Launcher.class);
        final var rabbitTemplate = springContext.getBean(RabbitTemplate.class);
        mapper.enable(SerializationFeature.INDENT_OUTPUT).setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

        rabbitTemplate.convertSendAndReceive("", "game_info", SAMPLE_RESPONSE_PAYLOAD, m -> {
            m.getMessageProperties().getHeaders().put("game_id", 1000);
            m.getMessageProperties().setContentType("appplication/json");
            return m;
        });
    }

}
