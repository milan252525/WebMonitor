package cz.cuni.mff.webmonitor.notifications;

import com.mailjet.client.*;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.transactional.*;
import com.mailjet.client.transactional.response.SendEmailsResponse;
import cz.cuni.mff.webmonitor.ResponseData;
import cz.cuni.mff.webmonitor.config.GlobalConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.http.HttpTimeoutException;

public class EmailNotifier implements INotifier {
    private static final Logger logger = LogManager.getLogger("WebMonitor");

    /**
     * Send an email notification
     *
     * @param data Response data object with notification config
     */
    @Override
    public void sendNotification(ResponseData data) {
        GlobalConfig gc = data.getServiceConfig().getGlobalConfig();

        ClientOptions options = ClientOptions.builder()
                .apiKey("d73b7f51b4e97dab0ae554b159182678")
                .apiSecretKey("dd804bfa9e3261222b88b5a0b80baacc")
                .build();

        MailjetClient client = new MailjetClient(options);

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
        String title = data.getServiceConfig().getURIAddress().toString();

        TransactionalEmail email = TransactionalEmail
                .builder()
                .to(new SendContact(gc.getEmailTo()))
                .from(new SendContact(gc.getEmailFrom()))
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
            logger.error("Error occurred while trying to send email:\n" + e.getMessage());
        }

        if (response != null)
            System.out.println(response.getMessages()[0]);
    }
}
