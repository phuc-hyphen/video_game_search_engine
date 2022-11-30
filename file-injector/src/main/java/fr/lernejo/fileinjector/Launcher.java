package fr.lernejo.fileinjector;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fr.lernejo.fileinjector.recorders.Game_info;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SpringBootApplication
public class Launcher {

    public static void main(String[] args) {
        final String game_path = "file-injector/src/main/java/fr/lernejo/fileinjector/games_c.json";
        final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        final AbstractApplicationContext springContext = new AnnotationConfigApplicationContext(Launcher.class);
        final var rabbitTemplate = springContext.getBean(RabbitTemplate.class);

        mapper.enable(SerializationFeature.INDENT_OUTPUT).setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        try {
            final List<Game_info> games = Arrays.asList(mapper.readValue(new File(game_path), Game_info[].class));
            String prettyMS = mapper.writeValueAsString(games);
//            System.out.println(prettyMS);
//            System.out.println("Hello after starting Spring");
            rabbitTemplate.convertAndSend("", "game_info", prettyMS); // sending message

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            springContext.close();
        }
    }
}
