package cz.cuni.mff.webmonitor;

import cz.cuni.mff.webmonitor.config.ServiceConfig;

import java.io.IOException;
import java.net.ConnectException;
import java.net.http.*;

public class HTTPRequester {
    private final HttpClient client;

    public HTTPRequester() {
        client = HttpClient.newHttpClient();
    }

    public void request(ServiceConfig serviceConfig) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(serviceConfig.getURIAddress())
                .timeout(serviceConfig.getTimeout())
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.toString());
            System.out.println(response.headers().map().toString());


        } catch (ConnectException e) {
            System.out.println("UNREACHABLE: " + e);
        } catch (HttpTimeoutException e) {
            System.out.println("TIMEOUT: " + e);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
