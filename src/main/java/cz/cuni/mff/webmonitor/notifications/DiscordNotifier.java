package cz.cuni.mff.webmonitor.notifications;

import cz.cuni.mff.webmonitor.ResponseData;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;

public class DiscordNotifier implements INotifier {
    /**
     * Send a notification
     *
     * @param data Response data object with notification config
     */
    @Override
    public void sendNotification(ResponseData data) {
        String webhookURL = data.getServiceConfig().getWebhook();
        String message;

        if (data.wasSuccess()) {
            message = "Status: " + data.getStatus();
        } else {
            if (data.getException() instanceof HttpTimeoutException)
                message = "Timeout: " + data.getException().getMessage();
            else
                message = "Exception: " + data.getException().getClass().toString() + ": " + data.getException().getMessage();
        }

        String title = data.getServiceConfig().getURIAddress().toString();
        String json =
                "{\"embeds\": [{"
                + "\"title\": \""+ title +"\","
                + "\"description\": \""+ message +"\","
                + "\"color\": 16711680"
                + "}]}";

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
