package cz.cuni.mff.webmonitor.notifications;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.transactional.SendContact;
import com.mailjet.client.transactional.SendEmailsRequest;
import com.mailjet.client.transactional.TransactionalEmail;
import com.mailjet.client.transactional.response.SendEmailsResponse;
import cz.cuni.mff.webmonitor.ResponseData;
import cz.cuni.mff.webmonitor.config.GlobalConfig;
import cz.cuni.mff.webmonitor.config.ServiceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.http.HttpTimeoutException;

import static cz.cuni.mff.webmonitor.Messages.messages;

public class EmailNotifier implements INotifier {
    private static final Logger logger = LogManager.getLogger("WebMonitor");

    /**
     * Send an email notification using MailJet service
     *
     * @param data Response data object with notification config
     */
    @Override
    public void sendNotification(ResponseData data) {
        ServiceConfig serviceConfig = data.getServiceConfig();
        GlobalConfig globalConfig = serviceConfig.getGlobalConfig();

        ClientOptions options = ClientOptions.builder()
                .apiKey(globalConfig.getEmailApiKey())
                .apiSecretKey(globalConfig.getEmailPrivateKey())
                .build();

        MailjetClient client = new MailjetClient(options);

        String message;
        // request successful but status regex matched
        if (data.wasSuccess()) {
            message = "Status: " + data.getStatus();
        } else {
            if (data.getException() instanceof HttpTimeoutException)
                message = "Timeout (%s): %s".formatted(serviceConfig.getTimeout().toString(), data.getException().getMessage());
            else
                message = "Exception: %s: %s".formatted(data.getException().getClass().toString(), data.getException().getMessage());
        }
        String title = data.getServiceConfig().getURIAddress().toString();

        TransactionalEmail email = TransactionalEmail
                .builder()
                .to(new SendContact(globalConfig.getEmailTo()))
                .from(new SendContact(globalConfig.getEmailFrom()))
                .textPart(message)
                .subject("ALERT: " + title)
                .build();

        SendEmailsRequest request = SendEmailsRequest
                .builder()
                .message(email)
                .build();

        SendEmailsResponse response = null;
        try {
            response = request.sendWith(client);
        } catch (MailjetException e) {
            logger.error("[{}] {}:\n{}", serviceConfig.getURIAddress(), messages.getString("EMAIL_FAIL"), e.getMessage());
        }
        if (response != null)
            logger.info("[{}] {}", serviceConfig, messages.getString("EMAIL_SUCCESS"));
    }
}
