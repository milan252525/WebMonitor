package cz.cuni.mff.webmonitor.notifications;

import cz.cuni.mff.webmonitor.ResponseData;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import javax.mail.Session;
import javax.mail.Transport;
import java.net.http.HttpResponse;
import java.util.Properties;

public class EmailNotifier implements INotifier {
    /**
     * Send an email notification
     *
     * @param data Response data object with notification config
     */
    @Override
    public void sendNotification(ResponseData data) {
        // :(
    }
}
