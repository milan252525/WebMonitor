package cz.cuni.mff.webmonitor.notifications;

import cz.cuni.mff.webmonitor.ResponseData;
import cz.cuni.mff.webmonitor.config.ServiceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URL;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;

import static cz.cuni.mff.webmonitor.Messages.messages;

public class DiscordNotifier implements INotifier {
    private static final Logger logger = LogManager.getLogger("WebMonitor");

    /**
     * Send a Discord webhook notification
     *
     * @param data Response data object with notification config
     */
    @Override
    public void sendNotification(ResponseData data) {
        ServiceConfig serviceConfig = data.getServiceConfig();
        String webhookURL = serviceConfig.getGlobalConfig().getWebhook();
        String message;

        // request successful but status regex matched
        if (data.wasSuccess()) {
            message = "Status: " + data.getStatus();
        } else {
            if (data.getException() instanceof HttpTimeoutException)
                message = "Timeout: " + data.getException().getMessage();
            else
                message = "Exception: " + data.getException().getClass().toString() + ": " + data.getException().getMessage();
        }

        String title = serviceConfig.getURIAddress().toString();
        // required embed JSON format, not using JSON library for those few lines
        String json = "{\"embeds\": [{\"title\": \"%s\",\"description\": \"%s\",\"color\": 16711680}]}".formatted(title, message);

        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(webhookURL).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("User-Agent", "Java-WebMonitor");
            connection.setDoOutput(true);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(json.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();

            connection.getInputStream().close();
            connection.disconnect();

            logger.info("[{}] {}", serviceConfig.getURIAddress(), messages.getString("DISCORD_SUCCESS"));
        } catch (Exception e) {
            logger.error("[{}] {}:\n{}", serviceConfig.getURIAddress(), messages.getString("DISCORD_FAIL"), e.getMessage());
        }
    }
}
