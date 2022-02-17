package cz.cuni.mff.webmonitor;

import cz.cuni.mff.webmonitor.config.NotifyLevel;
import cz.cuni.mff.webmonitor.config.ServiceConfig;
import cz.cuni.mff.webmonitor.notifications.DiscordNotifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.http.HttpTimeoutException;

/**
 * Class used for analyzing HTTP responses based on configuration
 */
public class ResponseAnalyzer {
    private static final Logger logger = LogManager.getLogger("WebMonitor");


    /**
     * Analyze HTTP response and choose corresponding actions (notifications)
     * @param data Response data object to analyze
     */
    public void analyze(ResponseData data) {
        ServiceConfig config = data.getServiceConfig();
        String address = config.getURIAddress().toString();
        if (!data.wasSuccess()) {
            if (data.getException() instanceof HttpTimeoutException) {
                logger.error("[{}] timed out after {}s" , address, config.getTimeout().getSeconds());
            } else {
                logger.error("[{}] exception occurred: {}" , address, data.getException().getMessage());
            }
            if (config.getNotifyLevel() == NotifyLevel.DISCORD) {
                new DiscordNotifier().sendNotification(data);
            }
        } else {
            String status = Integer.toString(data.getStatus());
            if (config.getStatusPattern().matcher(status).find()) {
                logger.error("[{}] {} - {}", address, status, data.getResponse().body());

                if (config.getNotifyLevel() == NotifyLevel.DISCORD) {
                    new DiscordNotifier().sendNotification(data);
                }
            } else {
                logger.info("[{}] {}" , address, status);
            }
        }
    }
}
