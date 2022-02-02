package cz.cuni.mff.webmonitor.config;

/**
 * Class holding configuration data for service non-specific data
 */
public class GlobalConfig {
    String email;

    /**
     * @return email
     */
    public String getEmail() {
        return email;
    }

    public boolean hasValidEmail() {
        return email != null;
    }
}
