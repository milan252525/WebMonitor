package cz.cuni.mff.webmonitor;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Config {
    private static final Yaml yaml = new Yaml();

    private String email;
    private String webAddress;
    private NotifyLevel notifyLevel;
    private String logFile;
    private List<String> statusList;
    private int secondsInterval;

    public static List<Config> loadFromFile(InputStreamReader inputStream) throws ConfigException {
        Map<String, Object> map = yaml.load(inputStream);

        String email = (String) map.getOrDefault("email", null);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> websites = (List<Map<String, Object>>) map.get("monitor");

        if (websites == null) {
            throw new ConfigException("List of websites to be monitored is missing! (Missing key \"monitor\")");
        }

        List<Config> result = new ArrayList<>();

        for (Map<String, Object> webMap: websites) {
            Config config = new Config();
            config.email = email;
            config.webAddress = (String) webMap.get("address");
            config.logFile = (String) webMap.get("log");

            Object status = webMap.get("status");
            if (status instanceof String statusStr) {
                config.statusList = new ArrayList<>();
                if (statusStr.equals("all") || statusStr.equals("any")) {
                    statusStr = "*";
                }
                config.statusList.add(statusStr);
            } else if (status instanceof List) {
                config.statusList = (List<String>) status;
            } else {
                throw new ConfigException("Status can be either \"all\", string or list of strings!");
            }

            Object notifyLevel = webMap.get("notify");
            String notify;
            if (notifyLevel instanceof Boolean) {
                notify = notifyLevel.toString();
            } else {
                notify = (String) notifyLevel;
            }
            if (notify == null) {
                config.notifyLevel = NotifyLevel.FALSE;
            }
            else if (notify.equals("email")) {
                config.notifyLevel = NotifyLevel.EMAIL;
            } else {
                config.notifyLevel = NotifyLevel.FALSE;
            }
            result.add(config);
        }

        return result;
    }

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
