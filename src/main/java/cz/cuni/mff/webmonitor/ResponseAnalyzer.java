package cz.cuni.mff.webmonitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.http.HttpTimeoutException;

public class ResponseAnalyzer {
    private static final Logger logger = LogManager.getLogger("WebMonitor");

    public void analyze(ResponseData data) {
        String address = data.getServiceConfig().getURIAddress().toString();
        if (!data.wasSuccess()) {
            if (data.getException() instanceof HttpTimeoutException) {
                logger.error("[{}] timed out after {}s" , address, data.getServiceConfig().getTimeout().getSeconds());
            } else {
                logger.error("[{}] exception occurred: {}" , address, data.getException().getMessage());
            }
        } else {
            if (data.getStatus() == 200) {
                logger.info("[{}] success" , address);
            } else {
                logger.error("[{}] {} - {}", address, data.getStatus(), data.getResponse().body());
            }
        }
    }
}
