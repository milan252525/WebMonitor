package cz.cuni.mff.webmonitor;

import cz.cuni.mff.webmonitor.config.ServiceConfig;

import java.net.http.*;

public class Requester {
    private final HttpClient client;

    public Requester() {
        client = HttpClient.newHttpClient();
    }

    public ResponseData request(ServiceConfig serviceConfig) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(serviceConfig.getURIAddress())
                .timeout(serviceConfig.getTimeout())
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return new ResponseData(response);

        } catch (Exception e) {
            return new ResponseData(response, e);
        }
    }
}
