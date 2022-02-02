package cz.cuni.mff.webmonitor;

import cz.cuni.mff.webmonitor.config.ServiceConfig;

import java.net.http.*;

/**
 * Class for handling HTTP requests
 */
public class Requester {
    private final HttpClient client;

    public Requester() {
        client = HttpClient.newHttpClient();
    }

    /** Send HTTP GET request and return response or exception
     * @param serviceConfig Monitored service configuration
     * @return ResponseData object carrying response or exception
     */
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
