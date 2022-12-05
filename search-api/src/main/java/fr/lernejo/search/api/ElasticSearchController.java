package fr.lernejo.search.api;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ElasticSearchController {
    private final RestHighLevelClient client;
//    final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();


    public ElasticSearchController(RestHighLevelClient client) {
        this.client = client;
    }

    @GetMapping("/api/games")
    public List<Map<String, Object>> getQuery(@RequestParam String query) {
        final List<Map<String, Object>> game_infos = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        .defaultOperator(Operator.AND)size(5)
        searchSourceBuilder.query(new QueryStringQueryBuilder(query)).size(20);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (ElasticsearchException e) {
            if (e.status() == RestStatus.CONFLICT) throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assert searchResponse != null; //TODO
        RestStatus status = searchResponse.status();
        System.out.println(status.toString());
        SearchHits hits = searchResponse.getHits();
//        TotalHits totalHits = hits.getTotalHits();
//        System.out.println(totalHits.value);

        SearchHit[] searchHits = hits.getHits();
//        mapper.enable(SerializationFeature.INDENT_OUTPUT).setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        for (SearchHit hit : searchHits) {
            // do something with the SearchHit
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//            Game_info gameInfo = mapper.convertValue(sourceAsMap, Game_info.class);
            game_infos.add(sourceAsMap);
        }
        return game_infos;
    }
}
