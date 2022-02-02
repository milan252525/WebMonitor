package cz.cuni.mff.webmonitor;

import cz.cuni.mff.webmonitor.config.ServiceConfig;

import java.net.http.HttpResponse;

public class ResponseData {
    private final HttpResponse<String> response;
    private final boolean exceptionOccurred;
    private final Exception exception;
    private final ServiceConfig serviceConfig;

    public ResponseData(HttpResponse<String> response, ServiceConfig config) {
        this.response = response;
        this.exceptionOccurred = false;
        this.exception = null;
        this.serviceConfig = config;
    }

    public ResponseData(HttpResponse<String> response, ServiceConfig config, Exception exception) {
        this.response = response;
        this.exceptionOccurred = true;
        this.exception = exception;
        this.serviceConfig = config;
    }

    public HttpResponse<String> getResponse() {
        return response;
    }

    public int getStatus() {
        return response.statusCode();
    }

    public boolean wasSuccess() {
        return !exceptionOccurred;
    }

    public Exception getException() {
        return exception;
    }

    public ServiceConfig getServiceConfig() {
        return this.serviceConfig;
    }
}