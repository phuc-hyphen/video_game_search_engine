package fr.lernejo.fileinjector;

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

@SpringBootApplication
public class Launcher {

    public static void main(String[] args) {
        final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        final AbstractApplicationContext springContext = new AnnotationConfigApplicationContext(Launcher.class);
        try (springContext) {
            SendingMessages(mapper, springContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void SendingMessages(ObjectMapper mapper, AbstractApplicationContext springContext) throws IOException {
        final var rabbitTemplate = springContext.getBean(RabbitTemplate.class);
        mapper.enable(SerializationFeature.INDENT_OUTPUT).setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        final Game_info[] games = mapper.readValue(new File("file-injector/src/main/java/fr/lernejo/fileinjector/games_c.json"), Game_info[].class);
        for (Game_info game : games) {
            String prettyMS = mapper.writeValueAsString(game);
//                System.out.println("print" + game.id());
            rabbitTemplate.convertAndSend("game_info", prettyMS, m -> {
                m.getMessageProperties().getHeaders().put("id", game.id());
                m.getMessageProperties().setContentType("appplication/json");
                return m;
            }); // sending message
        }
    }
}
