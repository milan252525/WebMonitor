package cz.cuni.mff.webmonitor.notifications;

import cz.cuni.mff.webmonitor.monitoring.ResponseData;

/**
 * Notification interface
 */
public interface INotifier {
    /**
     * Send a notification
     * @param data Response data object with notification config
     */
    void sendNotification(ResponseData data);
}
