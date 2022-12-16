package fr.lernejo.search.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
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

    public GameInfoListener(RestHighLevelClient rest) {
        this.client = rest;
    }

    @RabbitListener(queues = {GAME_INFO_QUEUE})
    void onMessage(final Message message) {

        IndexRequest request = new IndexRequest("games")
            .id(message.getMessageProperties().getHeaders().get("id").toString())
            .source(new String(message.getBody(), StandardCharsets.UTF_8), XContentType.JSON);
//        indexSync(request);

        indexAsync(request);
    }

    private void indexAsync(IndexRequest request) {
        ActionListener<IndexResponse> listener = new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(IndexResponse indexResponse) {
                System.out.println(indexResponse.status());
            }

            @Override
            public void onFailure(Exception e) {
                throw new RuntimeException(e);
            }
        };
        client.indexAsync(request, RequestOptions.DEFAULT, listener);
    }

    private void indexSync(IndexRequest request) {
        try {
            IndexResponse response = this.client.index(request, RequestOptions.DEFAULT);
            System.out.println(response.status());
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
