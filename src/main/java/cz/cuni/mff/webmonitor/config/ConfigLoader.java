package cz.cuni.mff.webmonitor.config;

import cz.cuni.mff.webmonitor.Constants;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.exceptions.ParserException;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static cz.cuni.mff.webmonitor.Messages.messages;

/**
 * Class with static methods for loading configuration
 */
public class ConfigLoader {

    /**
     * Instances disabled
     */
    private ConfigLoader() {}

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


        Object configObject;
        try {
            configObject = load.loadFromInputStream(inputStream);
        }
        catch (ParserException e) {
            throw new ConfigException(messages.getString("CONFIG_PARSE_EXCEPTION") + ":\n" + e.getMessage());
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) configObject;

        if (config == null) {
            throw new ConfigException(messages.getString("CONFIG_MISSING"));
        }

        // email isn't required
        if (config.containsKey("email")) {
            String email = (String) config.get("email");
            // the key can be present without value, ignore
            if (email != null ) {
                if (email.contains("@"))
                    globalConfig.email = email;
                else throw new ConfigException(messages.getString("EMAIL_INVALID") + " " + email);
            }
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
        if (map.containsKey(key))
            return map.get(key);

        if (addressForException != null)
            throw new ConfigException("[" + addressForException + "] " + messages.getString("KEY_" + key.toUpperCase()));
        else
            throw new ConfigException(messages.getString("KEY_" + key.toUpperCase()));
    }

    /**
     * Return value from a map if it exists, else return default value
     * @param map Map representing YAML configuration
     * @param key Key to look for
     * @param defaultReturn Default value to return
     * @return Value or defaultReturn
     */
    private static Object getOrDefault(Map<String, Object> map, String key, Object defaultReturn) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return defaultReturn;
    }

    /**
     * Load config for each service separately
     * @param services List of maps representing YAML configuration
     * @param globalConfig Global config for the application
     * @return  List of configuration for each monitored service
     * @throws ConfigException Configuration was not properly formatted
     */
    private static List<ServiceConfig> extractServices(List<Map<String, Object>> services,
                                                       GlobalConfig globalConfig) throws ConfigException {
        ArrayList<ServiceConfig> configs = new ArrayList<>();

        for (Map<String, Object> service: services) {
            ServiceConfig serviceConfig = new ServiceConfig(globalConfig);

            String address = (String) getIfPresent(service, "address");
            try {
                serviceConfig.URIAddress = new URI(address);
            } catch (URISyntaxException e) {
                throw new ConfigException("[" + address + "] " + messages.getString("ADDRESS_INVALID"));
            }

            //serviceConfig.logFile = (String) getIfPresent(service, "log", serviceConfig.URIAddress.toString());

            // accept all possibilities - null (no entry), String ("email", ...) or Boolean ("false")
            Object notifyLevelObject = getIfPresent(service, "notify", serviceConfig.URIAddress.toString()).toString();
            if (notifyLevelObject == null)
                serviceConfig.notifyLevel = NotifyLevel.FALSE;
            else {
                String notifyLevel = notifyLevelObject.toString();
                if ("email".equals(notifyLevel)) {
                    if (!globalConfig.hasValidEmail()) {
                        throw new ConfigException(messages.getString("EMAIL_MISSING"));
                    }
                    serviceConfig.notifyLevel = NotifyLevel.EMAIL;
                } else {
                    serviceConfig.notifyLevel = NotifyLevel.FALSE;
                }
            }

            String intervalString = (String) getIfPresent(service, "interval", serviceConfig.URIAddress.toString());
            Duration interval = strTimeToDuration(intervalString);

            if (interval == null)
                throw new ConfigException("[" + serviceConfig.URIAddress + "] " + messages.getString("INTERVAL_INVALID"));
            if (interval.getSeconds() < Constants.shortestInterval)
                throw new ConfigException("[" + serviceConfig.URIAddress + "] " + messages.getString("INTERVAL_SHORT"));
            serviceConfig.interval = interval;

            Duration timeout = strTimeToDuration((String) getOrDefault(service, "timeout", Constants.defaultTimeout));
            if (timeout == null)
                throw new ConfigException("[" + serviceConfig.URIAddress + "] " + messages.getString("TIMEOUT_INVALID"));
            serviceConfig.timeout = timeout;

            Object status = getOrDefault(service, "status", "any");
            if (status == null) // key present but no value
                status = "any";
            serviceConfig.statusPattern = statusToPattern(status.toString(), serviceConfig);

            configs.add(serviceConfig);
        }

        return configs;
    }

    /**
     * Convert time string into java.time.Duration
     * @param time Time string with format "00h00m00s", all time units are optional
     * @return Duration object representing interval, null if the input string was invalid
     */
    public static Duration strTimeToDuration(String time) {
        if (time == null)
            return null;
        time = time.toUpperCase();

        String ISOFormat;
        if (time.contains("D")) {
            ISOFormat = "P".concat(time.replace("D", "DT").toUpperCase());
            if (ISOFormat.endsWith("T")) {
                ISOFormat = ISOFormat.substring(0, ISOFormat.length()-1);
            }
        } else {
            ISOFormat = "PT".concat(time.toUpperCase());
        }

        try {
            return Duration.parse(ISOFormat);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Convert status from configuration to regular expression
     * @param status Status from config
     * @param serviceConfig Monitored service configuration
     * @return regex.Pattern object
     * @throws ConfigException Invalid regular expression
     */
    public static Pattern statusToPattern(String status, ServiceConfig serviceConfig) throws ConfigException {
        if (status.equals("all") || status.equals("any")) {
            status = "[3-9]..";
        }
        Pattern pattern;
        try {
            pattern = Pattern.compile(status, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            throw new ConfigException("[" + serviceConfig.URIAddress + "] "
                                            + messages.getString("STATUS_INVALID_REGEX")
                                            + " " + e.getDescription());
        }
        return pattern;
    }
}
