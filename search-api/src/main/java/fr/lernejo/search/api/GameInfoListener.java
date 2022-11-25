package fr.lernejo.search.api;


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
import java.util.Arrays;

import static fr.lernejo.search.api.AmqpConfiguration.GAME_INFO_QUEUE;

@Component
public class GameInfoListener {

    final RestHighLevelClient client;

    public GameInfoListener(RestHighLevelClient rest) {
        this.client = rest;
    }

    @RabbitListener(queues = {GAME_INFO_QUEUE})
    void onMessage(Message message) {

        String id = message.getMessageProperties().getHeaders().get("id").toString();
        String ms = Arrays.toString(message.getBody());
        //indexer le document
        IndexRequest request = new IndexRequest("games");
        request.id(id);
        String jsonString = "{" +
//                    "\"user\":\"client\"," +
//                    "\"postDate\":\"2022-11-25\"," +
            "\"message\":\"" + ms + "\"" + "}";
        request.source(jsonString, XContentType.JSON);
//        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        try {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            System.out.println(response.status().getStatus());
        } catch (ElasticsearchException e) {
            if (e.status() == RestStatus.CONFLICT) {
                throw e;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
