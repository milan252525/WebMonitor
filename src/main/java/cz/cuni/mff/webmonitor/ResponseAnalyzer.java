package cz.cuni.mff.webmonitor;

import cz.cuni.mff.webmonitor.config.NotifyLevel;
import cz.cuni.mff.webmonitor.config.ServiceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.http.HttpTimeoutException;

public class ResponseAnalyzer {
    private static final Logger logger = LogManager.getLogger("WebMonitor");

    public void analyze(ResponseData data) {
        ServiceConfig config = data.getServiceConfig();
        String address = config.getURIAddress().toString();
        if (!data.wasSuccess()) {
            if (data.getException() instanceof HttpTimeoutException) {
                logger.error("[{}] timed out after {}s" , address, config.getTimeout().getSeconds());
            } else {
                logger.error("[{}] exception occurred: {}" , address, data.getException().getMessage());
            }
        } else {
            String status = Integer.toString(data.getStatus());
            if (config.getStatusPattern().matcher(status).find()) {
                logger.error("[{}] {} - {}", address, status, data.getResponse().body());

                if (config.getNotifyLevel() == NotifyLevel.EMAIL) {
                    logger.info("TODO send email... ");
                }
            } else {
                logger.info("[{}] {} success" , address, status);
            }
        }
    }
}
