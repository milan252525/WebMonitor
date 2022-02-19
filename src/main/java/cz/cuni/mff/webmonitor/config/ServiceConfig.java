package cz.cuni.mff.webmonitor.config;

import java.net.URI;
import java.time.Duration;
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

    private final GlobalConfig globalConfig;

    /**
     * Initialize service configuration
     * @param globalConfig Instance of global configuration
     */
    public ServiceConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    @Override
    /**
     * String representation of config
     */
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
}
