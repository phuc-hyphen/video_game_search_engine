package fr.lernejo.search.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static fr.lernejo.search.api.AmqpConfiguration.GAME_INFO_QUEUE;

@Component
public class GameInfoListener {

    private final RestHighLevelClient client;
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public GameInfoListener(RestHighLevelClient rest) {
        this.client = rest;
    }

    @RabbitListener(queues = {GAME_INFO_QUEUE})
    void onMessage(final Message message) {

        //indexer le document
        IndexRequest request = new IndexRequest("games")
            .id(message.getMessageProperties().getHeaders().get("id").toString())
            .source(new String(message.getBody(), StandardCharsets.UTF_8), XContentType.JSON);
        try {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        } catch (ElasticsearchException e) {
            if (e.status() == RestStatus.CONFLICT)
                throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
//http://localhost:9200/games/_doc/1
//http://localhost:9200/_cat/indices/games*?v=true&s=index