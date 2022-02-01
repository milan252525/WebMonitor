package cz.cuni.mff.webmonitor.config;

/**
 * Custom exception class for configuration errors
 */
public class ConfigException extends Exception {

    /**
     * Constructs a new configuration exception with a given message
     * @param message Exception message
     */
    public ConfigException(String message) {
        super(message);
    }
}
