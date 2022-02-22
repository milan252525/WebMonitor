package cz.cuni.mff.webmonitor;

import cz.cuni.mff.webmonitor.config.ConfigException;
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
    public ResponseData request(ServiceConfig serviceConfig) throws ConfigException {
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(serviceConfig.getURIAddress())
                    .timeout(serviceConfig.getTimeout())
                    .GET()
                    .build();
        } catch (IllegalArgumentException e) {
            throw new ConfigException(Messages.messages.getString("URL_INVALID"));
        }

        HttpResponse<String> response = null;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ResponseData(response, serviceConfig);
        } catch (Exception e) {
            return new ResponseData(response, serviceConfig, e);
        }
    }
}
