package client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final URI uri;
    private final HttpClient httpClient;
    private final HttpResponse.BodyHandler<String> handler;
    private static String API_TOKEN;

    public KVTaskClient(URI uri) throws IOException, InterruptedException {

        this.uri = uri;
        httpClient = HttpClient.newHttpClient();
        handler = HttpResponse.BodyHandlers.ofString();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri +
                        "/register"))
                .build();

        HttpResponse<String> response = httpClient.send(request, handler);
        API_TOKEN = response.body();

        System.out.println("Код ответа при регистрации: " + response.statusCode());
        System.out.println("Получили API_TOKEN: " + API_TOKEN);
    }

    public void put(String key, String json) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(uri + "/save/" + key + "?API_TOKEN=" + API_TOKEN))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .header("Authorization", "OAuth " + API_TOKEN)
                .build();
        HttpResponse<String> response = httpClient.send(request, handler);
        System.out.println("Код ответа при сохранении в хранилище: "
                + response.statusCode());
    }


    public String load(String key) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "/load/" + key + "?API_TOKEN=" + API_TOKEN))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, handler);
        System.out.println("Код ответа при сохранении в хранилище: "
                + response.statusCode());
        return response.body();

    }
}
