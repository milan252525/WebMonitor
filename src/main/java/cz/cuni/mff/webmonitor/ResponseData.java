package cz.cuni.mff.webmonitor;

import java.net.http.HttpResponse;

public class ResponseData {
    private final HttpResponse<String> response;
    private final boolean exceptionOccurred;
    private final Exception exception;

    public ResponseData(HttpResponse<String> response) {
        this.response = response;
        this.exceptionOccurred = false;
        this.exception = null;
    }

    public ResponseData(HttpResponse<String> response, Exception exception) {
        this.response = response;
        this.exceptionOccurred = true;
        this.exception = exception;
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
}
