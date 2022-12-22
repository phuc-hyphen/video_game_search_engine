package fr.lernejo.search.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.IOException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

import java.util.concurrent.TimeoutException;


class GameInfoListenerTest {
    String SAMPLE_RESQUEST_PAYLOAD = """
        {
              "id": 1,
              "title": "Dauntless",
              "thumbnail": "https:\\/\\/www.freetogame.com\\/g\\/1\\/thumbnail.jpg",
              "short_description": "A free-to-play, co-op action RPG with gameplay similar to Monster Hunter.",
              "game_url": "https:\\/\\/www.freetogame.com\\/open\\/dauntless",
              "genre": "MMORPG",
              "platform": "PC (Windows)",
              "publisher": "Phoenix Labs",
              "developer": "Phoenix Labs, Iron Galaxy",
              "release_date": "2019-05-21",
              "freetogame_profile_url": "https:\\/\\/www.freetogame.com\\/dauntless"
        }
        """.stripTrailing();

    public record Game_info(int id, String title, String thumbnail, String short_description, String game_url,
                            String genre, String platform, String publisher, String developer,
                            @JsonSerialize(using = LocalDateSerializer.class) LocalDate release_date,
                            String freetogame_profile_url) {
    }

    @Test
    void adding_game_test() {
        IOException test = null;
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
            final Game_info game_test = mapper.readValue(SAMPLE_RESQUEST_PAYLOAD, Game_info.class);
            final AbstractApplicationContext springContext = new AnnotationConfigApplicationContext(Launcher.class);
            final var rabbitTemplate = springContext.getBean(RabbitTemplate.class);
            mapper.enable(SerializationFeature.INDENT_OUTPUT).setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            String str_game = mapper.writeValueAsString(game_test);
            rabbitTemplate.convertSendAndReceive("", "game_info", str_game, m -> {
                m.getMessageProperties().getHeaders().put("game_id", game_test.id());
                m.getMessageProperties().setContentType("appplication/json");
                return m;
            });
            channel.close();
            connection.close();

        } catch (IOException | TimeoutException e) {
            test = (IOException) e;
        }
    }


}
