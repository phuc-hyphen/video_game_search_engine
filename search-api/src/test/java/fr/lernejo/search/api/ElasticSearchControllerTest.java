package fr.lernejo.search.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ElasticSearchControllerTest {

    String SAMPLE_RESPONSE_PAYLOAD = """
        {
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
    void checkHomePage(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.status().isOk());
    }
//        System.out.println(response.body());


    void CheckGetGames(@Autowired MockMvc mockMvc, String query, int ExpResult) throws Exception {

        MvcResult result = mockMvc
            .perform(MockMvcRequestBuilders.get("/api/games?query=" + query ))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        String content = result.getResponse().getContentAsString();
        try {
            // convert JSON string to Map
            List<Map<String, Object>> resultmap =
                mapper.readValue(content, new TypeReference<List<Map<String, Object>>>() {
                });
            assertEquals(ExpResult, resultmap.size(), "not good number response");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    void Get2GamesDeveloppedbyEpicGame(@Autowired MockMvc mockMvc) throws Exception {
        CheckGetGames(mockMvc,"developer:\"Epic Games\"",2);
    }

    @Test
    void GetGamesComplex(@Autowired MockMvc mockMvc) throws Exception {
        CheckGetGames(mockMvc,"genre:Strategy AND developer:\"Epic Games\"",1);
    }
}
