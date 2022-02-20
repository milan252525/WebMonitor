package cz.cuni.mff.webmonitor.config;

import cz.cuni.mff.webmonitor.Constants;
import cz.cuni.mff.webmonitor.notifications.INotifier;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * Class holding configuration of a specific monitored service
 */
public class ServiceConfig {
    protected URI URIAddress;
    protected NotifyLevel notifyLevel;
    protected Pattern statusPattern;
    protected Duration interval;
    protected Duration timeout;
    protected INotifier notifier;
    private LocalDateTime lastNotification = null;

    private final GlobalConfig globalConfig;

    /**
     * Initialize service configuration
     * @param globalConfig Instance of global configuration
     */
    public ServiceConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    @Override
    public String toString() {
        return "ServiceConfig{" +
                "URIAddress=" + URIAddress +
                ", notifyLevel=" + notifyLevel +
                ", statusPattern=" + statusPattern +
                ", interval=" + interval +
                ", timeout=" + timeout +
                ", globalConfig=" + globalConfig +
                '}';
    }

    /**
     * URI address
     */
    public URI getURIAddress() {
        return URIAddress;
    }

    /**
     * Notification level
     */
    public NotifyLevel getNotifyLevel() {
        return notifyLevel;
    }

    /**
     * Status regex pattern
     */
    public Pattern getStatusPattern() {
        return statusPattern;
    }

    /**
     * Monitoring interval
     */
    public Duration getInterval() {
        return interval;
    }

    /**
     * Request timeout
     */
    public Duration getTimeout() {
        return timeout;
    }

    /**
     * Global configuration
     */
    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    /**
     * Notifier
     */
    public INotifier getNotifier() {
        return notifier;
    }

    /**
     * Set last notification time to now
     */
    public void setLastNotification() {
        this.lastNotification = LocalDateTime.now();
    }

    public boolean canNotifyAgain() {
        if (this.lastNotification == null)
            return true;
        return this.lastNotification.plusMinutes(Constants.notificationDelayMinutes).isBefore(LocalDateTime.now()) ;
    }
}
