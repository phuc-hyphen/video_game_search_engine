package fr.lernejo.fileinjector;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

class LauncherTest {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();


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
    void Launcher_WITH_GOOD_ARGUMENT_Test() {
        String[] args = {"/home/huu-phuc-le/JAVA/video_game_search_engine/file-injector/src/test/resources/games.json"};
        IOException test = null;
        try {
            Launcher.main(args);
        } catch (IOException e) {
            test = e;
        }
        Assertions.assertThat(test).isNull();
    }

    @Test
    void Launcher_WITH_BAD_ARGUMENT_Test() {
        String[] args = {"/home/huu-phuc-le/JAVA/video_game_search_engine/file-injector/src/test/resources/not_game.json"};
        IOException test = null;
        try {
            Launcher.main(args);
        } catch (IOException e) {
            test = e;
        }
        Assertions.assertThat(test).isNotNull();

    }
}
