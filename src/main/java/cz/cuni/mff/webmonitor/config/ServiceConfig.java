package cz.cuni.mff.webmonitor.config;

import java.net.URI;
import java.time.Duration;
import java.util.regex.Pattern;

/**
 * Class holding configuration of a specific monitored service
 */
public class ServiceConfig {
    URI URIAddress;
    NotifyLevel notifyLevel;
    String logFile;
    Pattern statusPattern;
    Duration interval;
    Duration timeout;

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
                ", logFile='" + logFile + '\'' +
                ", statusPattern=" + statusPattern.toString() +
                ", interval=" + interval +
                ", timeout=" + timeout +
                '}';
    }

    public URI getURIAddress() {
        return URIAddress;
    }

    public NotifyLevel getNotifyLevel() {
        return notifyLevel;
    }

    public String getLogFile() {
        return logFile;
    }

    public Pattern getStatusPattern() {
        return statusPattern;
    }

    public Duration getInterval() {
        return interval;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public String getEmail() {
        return globalConfig.getEmail();
    }

    public String getWebhook() { return globalConfig.getWebhook(); }
}
