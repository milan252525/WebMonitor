package cz.cuni.mff.webmonitor.config;

/**
 * Class holding configuration data for service non-specific data
 */
public class GlobalConfig {
    String email;
    String webhook;

    /**
     * @return email
     */
    public String getEmail() {
        return email;
    }

    public boolean hasValidEmail() {
        return email != null;
    }

    public boolean hasWebhook() {
        return webhook != null && !webhook.equals("");
    }

    public String getWebhook() {
        return webhook;
    }
}
