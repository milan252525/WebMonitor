package cz.cuni.mff.webmonitor;

import cz.cuni.mff.webmonitor.config.GlobalConfig;
import cz.cuni.mff.webmonitor.config.ServiceConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLSession;
import java.net.URI;
import java.net.http.*;
import java.util.Optional;

class ResponseDataTest {
    @Test
    void testRequestData() {
        HttpResponse<String> fakeResponse = new HttpResponse<String>() {
            @Override
            public int statusCode() {
                return 500;
            }

            @Override
            public HttpRequest request() {
                return null;
            }

            @Override
            public Optional<HttpResponse<String>> previousResponse() {
                return Optional.empty();
            }

            @Override
            public HttpHeaders headers() {
                return null;
            }

            @Override
            public String body() {
                return null;
            }

            @Override
            public Optional<SSLSession> sslSession() {
                return Optional.empty();
            }

            @Override
            public URI uri() {
                return null;
            }

            @Override
            public HttpClient.Version version() {
                return null;
            }
        };

        Exception fakeException = new HttpTimeoutException("message");
        ServiceConfig fakeConfig = new ServiceConfig(new GlobalConfig());
        ResponseData rd = new ResponseData(fakeResponse, fakeConfig, fakeException);

        Assertions.assertFalse(rd.wasSuccess());
        Assertions.assertEquals(500, rd.getStatus());
        Assertions.assertEquals(fakeException, rd.getException());
    }
}