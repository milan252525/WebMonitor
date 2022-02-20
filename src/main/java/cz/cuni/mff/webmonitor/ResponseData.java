package cz.cuni.mff.webmonitor;

import cz.cuni.mff.webmonitor.config.ServiceConfig;

import java.net.http.HttpResponse;

/**
 * Class for wrapping HTTP response, exception and config into one object
 */
public class ResponseData {
    private final HttpResponse<String> response;
    private final boolean exceptionOccurred;
    private final Exception exception;
    private final ServiceConfig serviceConfig;

    /**
     * Constructor for successful response
     * @param response Response object
     * @param config Service configuration
     */
    public ResponseData(HttpResponse<String> response, ServiceConfig config) {
        this.response = response;
        this.exceptionOccurred = false;
        this.exception = null;
        this.serviceConfig = config;
    }

    /**
     * Constructor for unsuccessful response with exception
     * @param response Response object
     * @param config Service configuration
     * @param exception Exception which occurred
     */
    public ResponseData(HttpResponse<String> response, ServiceConfig config, Exception exception) {
        this.response = response;
        this.exceptionOccurred = true;
        this.exception = exception;
        this.serviceConfig = config;
    }

    /**
     * HTTP response
     */
    public HttpResponse<String> getResponse() {
        return response;
    }

    /**
     * HTTP response status
     * @return HTTP status
     */
    public int getStatus() {
        return response.statusCode();
    }

    /**
     * Response success
     * @return true if the response is valid, false if exception occurred
     */
    public boolean wasSuccess() {
        return !exceptionOccurred;
    }

    /**
     * Exception which occurred
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Service configuration
     */
    public ServiceConfig getServiceConfig() {
        return this.serviceConfig;
    }
}
