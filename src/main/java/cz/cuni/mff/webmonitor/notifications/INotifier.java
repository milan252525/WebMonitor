package cz.cuni.mff.webmonitor.notifications;

import cz.cuni.mff.webmonitor.ResponseData;

public interface INotifier {
    /**
     * Send a notification
     * @param data Response data object with notification config
     */
    public void sendNotification(ResponseData data);
}
