package fr.lernejo.search.api;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

class ElasticSearchConfigurationTest {

    RestHighLevelClient restClient(
    ) {
        final CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "admin"));
        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200)).setHttpClientConfigCallback(
            httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(provider));
        return new RestHighLevelClient(builder);
    }

//    @Test
//    void getindex() throws IOException {
////        Launcher.main(new String[]{});
//        RestHighLevelClient client = restClient();
//        GetIndexRequest request = new GetIndexRequest("games");
//        GetIndexResponse getIndexResponse = client.indices().get(request, RequestOptions.DEFAULT);
//        MappingMetadata indexMappings = getIndexResponse.getMappings().get("games");
//        Map<String, Object> indexTypeMappings = indexMappings.getSourceAsMap();
//        assertNotEquals(0, indexTypeMappings.size());
//    }


}
