package fr.lernejo.fileinjector;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.MountableFile;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LauncherTest {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //    @ClassRule
//    public static DockerComposeContainer environment =
//        new DockerComposeContainer(new File("src/test/resources/compose-test.yml"))
//            .withExposedService("rabbitmq", DEFAULT_AMQP_PORT)
//            .withExposedService("rabbitmq", DEFAULT_HTTP_PORT);

    @ClassRule
    public static GenericContainer rabbitMQContainer =
        new GenericContainer("rabbitmq:3.9.13-management-alpine")
            .withExposedPorts(5672, 15672)
            .waitingFor(Wait.forListeningPort());

    @Test
    void Launcher_WITH_GOOD_ARGUMENT_Test() {
//            RabbitMQTestImages.RABBITMQ_IMAGE
//        final RabbitMQContainer environment =
//            new RabbitMQContainer("rabbitmq:3.9.13-management-alpine")
//                .withExposedPorts(DEFAULT_AMQP_PORT, DEFAULT_HTTP_PORT);

        IOException test = null;
        try {
//            RabbitMQContainer rabbitMQContainer = new RabbitMQContainer();
//            rabbitMQContainer.start();
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            Launcher.main(new String[]{"../file-injector/src/test/resources/games.json"});

            channel.close();
            connection.close();
//            rabbitMQContainer.stop();


        } catch (IOException | TimeoutException e) {
            test = (IOException) e;
        }
//        Assertions.assertThat(test).isNull();
    }

    @Test
    void main_terminates_before_5_sec() {
        assertTimeoutPreemptively(
            Duration.ofSeconds(5L),
            () -> Launcher.main(new String[]{}));
    }

    @Test
    void Launcher_NO_ARGUMENT_Test() {
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        String[] args = {};
        IOException test = null;
        try {
            Launcher.main(args);
        } catch (IOException e) {
            test = e;
        }
        Assertions.assertThat(test).isNull();
        Assertions.assertThat(baos.toString()).contains("Argument missing : At least one for the path of the resource !!! ");

        System.out.flush();
        System.setOut(old);
    }


    @Test
    void Launcher_WITH_BAD_ARGUMENT_Test() {
        String[] args = {"../file-injector/src/test/resources/not_game.json"};
        IOException test = null;
        try {
            Launcher.main(args);
        } catch (IOException e) {
            test = e;
        }
        Assertions.assertThat(test).isNotNull();

    }
}
