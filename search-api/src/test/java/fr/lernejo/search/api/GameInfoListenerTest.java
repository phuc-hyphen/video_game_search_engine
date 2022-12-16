package fr.lernejo.search.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class GameInfoListenerTest {
    @Test
    void check_existant_of_doc(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders.get("http://localhost:9200/games/_doc/1")).andExpect(MockMvcResultMatchers.status().isNotFound());
//        Launcher.main(new String[]{});
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest getRequest = HttpRequest.newBuilder()
//            .uri(URI.create("http://localhost:8080"))
//            .build();
//        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
//        Assertions.assertThat(response.statusCode()).isEqualTo(200);
    }

}
