package cz.cuni.mff.webmonitor.config;

/**
 * Class holding configuration data for service non-specific data
 */
public class GlobalConfig {
    protected String emailApiKey;
    protected String emailPrivateKey;
    protected String emailFrom;
    protected String emailTo;
    protected String webhook;

    /**
     * Check if all email fields are set
     */
    public boolean hasValidEmailConfig() {
        return  getEmailApiKey() != null
                && getEmailPrivateKey() != null
                && getEmailFrom() != null
                && getEmailTo() != null;
    }

    /**
     * Check if webhook is set
     */
    public boolean hasWebhook() {
        return webhook != null && !webhook.equals("");
    }

    /**
     * Webhook
     */
    public String getWebhook() {
        return webhook;
    }

    /**
     * Email API key
     */
    public String getEmailApiKey() {
        return emailApiKey;
    }

    /**
     * Email private API key
     */
    public String getEmailPrivateKey() {
        return emailPrivateKey;
    }

    /**
     * Email sender
     */
    public String getEmailFrom() {
        return emailFrom;
    }

    /**
     * Email recipient
     */
    public String getEmailTo() {
        return emailTo;
    }
}
