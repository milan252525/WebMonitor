package cz.cuni.mff.webmonitor.config;

import cz.cuni.mff.webmonitor.NotifyLevel;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

import java.io.InputStream;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cz.cuni.mff.webmonitor.Messages.messages;

public class ConfigLoader {

    /**
     * Load configuration from YAML file, consult documentation for proper format
     * @param inputStream Input stream to read from
     * @return  List of configuration for each monitored service
     * @throws ConfigException Configuration was not properly formatted
     */
    public static List<ServiceConfig> loadFromFile(InputStream inputStream) throws ConfigException {
        LoadSettings settings = LoadSettings.builder().setLabel("WebMonitor configuration").build();
        Load load = new Load(settings);

        GlobalConfig globalConfig = new GlobalConfig();

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) load.loadFromInputStream(inputStream);

        if (config == null) {
            throw new ConfigException(messages.getString("CONFIG_MISSING"));
        }

        if (config.containsKey("email")) {
            globalConfig.email = (String) config.get("email");
        }

        if (!config.containsKey("services")) {
            throw new ConfigException(messages.getString("KEY_SERVICES"));
        }

        @SuppressWarnings("unchecked")
        var services = (ArrayList<Map<String, Object>>) config.get("services");

        return extractServices(services, globalConfig);
    }

    /**
     * Return value from a map if it exists, else raise ConfigException
     * @param map Map representing YAML configuration
     * @param key Key to look for
     * @return Value if it is present
     * @throws ConfigException Value is not present
     */
    @SuppressWarnings("SameParameterValue")
    private static Object getIfPresent(Map<String, Object> map, String key) throws ConfigException {
        return getIfPresent(map, key, null);
    }

    /**
     * Return value from a map if it exists, else raise ConfigException
     * @param map Map representing YAML configuration
     * @param key Key to look for
     * @param addressForException Service address to print in case of config exception
     * @return Value if it is present
     * @throws ConfigException Value is not present
     */
    private static Object getIfPresent(Map<String, Object> map, String key, String addressForException) throws ConfigException {
        if (!map.containsKey(key)) {
            if (addressForException != null) {
                addressForException = "[" + addressForException + "] ";
            }
            throw new ConfigException(addressForException + messages.getString("KEY_" + key.toUpperCase()));
        } else {
            return map.get(key);
        }
    }

    /**
     * Load config for each service separately
     * @param services List of maps representing YAML configuration
     * @param globalConfig Global config for the application
     * @return  List of configuration for each monitored service
     * @throws ConfigException Configuration was not properly formatted
     */
    private static List<ServiceConfig> extractServices(List<Map<String, Object>> services, GlobalConfig globalConfig) throws ConfigException {
        ArrayList<ServiceConfig> configs = new ArrayList<>();

        for (Map<String, Object> service: services) {
            ServiceConfig serviceConfig = new ServiceConfig(globalConfig);
            serviceConfig.webAddress = (String) getIfPresent(service, "address");
            serviceConfig.logFile = (String) getIfPresent(service, "log", serviceConfig.webAddress);

            // accept all possibilities - null (no entry), String ("email", ...) or Boolean ("false")
            Object notifyLevelObject = getIfPresent(service, "notify", serviceConfig.webAddress).toString();
            if (notifyLevelObject == null)
                serviceConfig.notifyLevel = NotifyLevel.FALSE;
            else {
                String notifyLevel = notifyLevelObject.toString();
                switch (notifyLevel) {
                    case "email"    ->  serviceConfig.notifyLevel = NotifyLevel.EMAIL;
                    default         ->  serviceConfig.notifyLevel = NotifyLevel.FALSE;
                }
            }

            String interval = (String) getIfPresent(service, "interval", serviceConfig.webAddress);
            serviceConfig.interval = strTimeToDuration(interval.toUpperCase(), serviceConfig.webAddress);

            configs.add(serviceConfig);
        }

        return configs;
    }

    /**
     * Convert time string into java.time.Duration
     * @param time Time string with format "00h00m00s", all time units are optional
     * @param addressForException Service address to print in case of config exception
     * @return Duration object representing interval
     * @throws ConfigException Invalid input string or too short interval (< 10s)
     */
    private static Duration strTimeToDuration(String time, String addressForException) throws ConfigException {
        Duration duration;
        String ISOFormat;
        if (time.contains("D")) {
            ISOFormat = "P".concat(time.replace("d", "DT").toUpperCase());
        } else {
            ISOFormat = "PT".concat(time.toUpperCase());
        }

        try {
            duration = Duration.parse(ISOFormat);
        } catch (DateTimeParseException e) {
            throw new ConfigException("[" + addressForException + "] " + messages.getString("INTERVAL_INVALID"));
        }

        if (duration.isNegative() || duration.getSeconds() < 10) {
            throw new ConfigException("[" + addressForException + "] " + messages.getString("INTERVAL_SHORT"));
        }
        return duration;
    }
}
