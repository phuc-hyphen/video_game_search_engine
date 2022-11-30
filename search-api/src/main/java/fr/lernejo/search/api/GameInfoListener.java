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

import static fr.lernejo.search.api.AmqpConfiguration.GAME_INFO_QUEUE;

@Component
public class GameInfoListener {

    final RestHighLevelClient client;
    final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public GameInfoListener(RestHighLevelClient rest) {
        this.client = rest;
    }

    @RabbitListener(queues = {GAME_INFO_QUEUE})
    void onMessage(final Message message) {

        String id = message.getMessageProperties().getHeaders().get("id").toString();
        String messBody = new String(message.getBody(), StandardCharsets.UTF_8);
        System.out.println(messBody);
        //indexer le document
        IndexRequest request = new IndexRequest("games");
        request.id(id).source(messBody, XContentType.JSON);
        try {
//            String ms = mapper.writeValueAsString(messBody);
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
//            System.out.println(response.status().getStatus());
        } catch (ElasticsearchException e) {
            if (e.status() == RestStatus.CONFLICT) {
                throw e;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
//application/json
//https://www.tabnine.com/code/java/classes/org.springframework.amqp.core.Message
