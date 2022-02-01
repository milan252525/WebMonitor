package cz.cuni.mff.webmonitor.config;

import cz.cuni.mff.webmonitor.NotifyLevel;

import java.time.Duration;
import java.util.List;

public class ServiceConfig {
    String webAddress;
    NotifyLevel notifyLevel;
    String logFile;
    List<String> statusList;
    Duration interval;
    GlobalConfig globalConfig;

    public ServiceConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    @Override
    public String toString() {
        return "ServiceConfig{" +
                "webAddress='" + webAddress + '\'' +
                ", notifyLevel=" + notifyLevel +
                ", logFile='" + logFile + '\'' +
                ", statusList=" + statusList +
                ", interval=" + interval +
                ", globalConfig=" + globalConfig.toString() +
                '}';
    }

    public String getWebAddress() {
        return webAddress;
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
}
