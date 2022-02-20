package cz.cuni.mff.webmonitor;

/**
 * Application constants needed in multiple places
 */
public final class Constants {
    /**
     * No instances
     */
    private Constants() {}

    /**
     * Default HTTP request timeout for monitoring
     */
    public final static String defaultTimeout = "5m";

    /**
     * Seconds of the shortest possible interval for monitoring
     */
    public final static long shortestIntervalSeconds = 10;

    /**
     * Minutes to wait before sending another notification if error persists
     */
    public final static long notificationDelayMinutes = 10;
}
