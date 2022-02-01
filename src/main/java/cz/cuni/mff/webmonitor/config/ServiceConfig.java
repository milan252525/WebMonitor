package cz.cuni.mff.webmonitor.config;

import cz.cuni.mff.webmonitor.NotifyLevel;

import java.net.URI;
import java.time.Duration;
import java.util.List;

public class ServiceConfig {
    URI URIAddress;
    NotifyLevel notifyLevel;
    String logFile;
    List<String> statusList;
    Duration interval;
    Duration timeout;
    GlobalConfig globalConfig;

    public ServiceConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    @Override
    public String toString() {
        return "ServiceConfig{" +
                "URIAddress=" + URIAddress +
                ", notifyLevel=" + notifyLevel +
                ", logFile='" + logFile + '\'' +
                ", statusList=" + statusList +
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

    public List<String> getStatusList() {
        return statusList;
    }

    public Duration getInterval() { return interval; }

    public Duration getTimeout() { return timeout; }
}
