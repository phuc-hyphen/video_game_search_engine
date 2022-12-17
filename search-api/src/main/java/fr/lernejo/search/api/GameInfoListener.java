package fr.lernejo.search.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.cluster.metadata.MappingMetadata;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

import static fr.lernejo.search.api.AmqpConfiguration.GAME_INFO_QUEUE;

@Component
public class GameInfoListener {

    private final RestHighLevelClient client;
    private final String index = "games";

    public GameInfoListener(RestHighLevelClient rest) {
        this.client = rest;
    }

    @RabbitListener(queues = {GAME_INFO_QUEUE})
    public void onMessage(final Message message) throws IOException {
        String id = message.getMessageProperties().getHeaders().get("game_id").toString();
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        GetIndexRequest getIndexRequest = new GetIndexRequest(index);
        if (!client.indices().exists(getIndexRequest, RequestOptions.DEFAULT)) {
            CreateIndex(body);
        } else {
            Indexing_games(id, message);
        }
    }

    private void Indexing_games(String id, Message message) throws IOException {
        IndexRequest request = new IndexRequest("games");
        request.id(id).source(new String(message.getBody(), StandardCharsets.UTF_8), XContentType.JSON);
        IndexResponse response = this.client.index(request, RequestOptions.DEFAULT);
        System.out.println("new game indexed !!!");
    }

    private void CreateIndex(String body) throws IOException {
        CreateIndexRequest creatIndexRequest = new CreateIndexRequest(index);
        creatIndexRequest.settings(Settings.builder()
            .put("index.number_of_shards", 3)
            .put("index.number_of_replicas", 2));
//            .source(body, XContentType.JSON);
        client.indices().create(creatIndexRequest, RequestOptions.DEFAULT);
        System.out.println("new index created !!!");
    }
    //https://discuss.elastic.co/t/compressor-detection-can-only-be-called-on-some-xcontent-bytes/184959
//    private void indexSync(IndexRequest request) {
//        try {
//            IndexResponse response = client.indices().create(creatIndexRequest, RequestOptions.DEFAULT);
//                System.out.println(response.status());
//        } catch (ElasticsearchException e) {
//            if (e.status() == RestStatus.CONFLICT)
//                throw e;
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
//http://localhost:9200/games/_doc/1
//http://localhost:9200/_cat/indices/games*?v=true&s=index
