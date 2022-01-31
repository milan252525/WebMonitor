package cz.cuni.mff.webmonitor;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class HTTPRequester {
    private final HttpClient client;

    public HTTPRequester() {
        client = HttpClient.newHttpClient();
    }


    public void request(String address) {
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(address))
                    .timeout(Duration.of(5, ChronoUnit.SECONDS))
                    .GET().build();

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.toString());
            System.out.println(response.headers().map().toString());


        } catch (ConnectException e) {
            System.out.println("UNREACHABLE: " + e);
        }

        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
