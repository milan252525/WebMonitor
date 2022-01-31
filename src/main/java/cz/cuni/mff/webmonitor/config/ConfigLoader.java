package cz.cuni.mff.webmonitor.config;

import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static cz.cuni.mff.webmonitor.Messages.messages;

public class ConfigLoader {

    public static List<ServiceConfig> loadFromFile(InputStream inputStream) throws ConfigException {
        LoadSettings settings = LoadSettings.builder().setLabel("WebMonitor configuration").build();
        Load load = new Load(settings);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) load.loadFromInputStream(inputStream);

        if (config == null) {
            throw new ConfigException(messages.getString("CONFIG_MISSING"));
        }

        String email = null;
        if (config.containsKey("email")) {
            email = (String) config.get("email");
        }

        if (!config.containsKey("services")) {
            throw new ConfigException(messages.getString("KEY_SERVICES"));
        }

        @SuppressWarnings("unchecked")
        var services = (ArrayList<Map<String, Object>>) config.get("services");

        List<ServiceConfig> results = extractServices(services, email);

        return results;
    }

    private static Object getCheck(Map<String, Object> map, String key) throws ConfigException {
        if (!map.containsKey(key)) {
            throw new ConfigException(messages.getString("KEY_" + key.toUpperCase()));
        } else {
            return map.get(key);
        }
    }

    private static List<ServiceConfig> extractServices(List<Map<String, Object>> services, String email) throws ConfigException {
        ArrayList<ServiceConfig> configs = new ArrayList<>();

        for (Map<String, Object> service: services) {
            ServiceConfig serviceConfig = new ServiceConfig();
            serviceConfig.webAddress = (String) getCheck(service, "address");
            serviceConfig.logFile = (String) getCheck(service, "log");

            configs.add(serviceConfig);
        }

        return configs;
    }
}
