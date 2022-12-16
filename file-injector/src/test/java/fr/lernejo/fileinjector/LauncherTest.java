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
        IOException test = new IOException();
        try {
            Launcher.main(args);
        } catch (IOException e) {
            test = e;
        }
        Assertions.assertThat(test).isNotNull();
        Assertions.assertThat(baos.toString()).contains("Argument missing : At least one for the path of the resource !!! ");

        System.out.flush();
        System.setOut(old);
    }
}
