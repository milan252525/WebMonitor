package cz.cuni.mff.webmonitor.config;

import cz.cuni.mff.webmonitor.NotifyLevel;

import java.util.List;

public class ServiceConfig {
    String email;
    String webAddress;
    NotifyLevel notifyLevel;
    String logFile;
    List<String> statusList;
    int secondsInterval;

    @Override
    public String toString() {
        return "Config{" +
                "email='" + email + '\'' +
                ", webAddress='" + webAddress + '\'' +
                ", notifyLevel=" + notifyLevel +
                ", logFile='" + logFile + '\'' +
                ", statusList=" + statusList +
                ", secondsInterval=" + secondsInterval +
                '}';
    }

    public String getEmail() {
        return email;
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

    public int getSecondsInterval() {
        return secondsInterval;
    }
}
