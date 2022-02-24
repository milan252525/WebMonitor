package cz.cuni.mff.webmonitor.monitoring;

import cz.cuni.mff.webmonitor.config.ConfigException;
import cz.cuni.mff.webmonitor.config.ServiceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class representing a single monitored service
 */
public class MonitorTask implements Runnable {
    private static final Logger logger = LogManager.getLogger("WebMonitor");

    private final ServiceConfig config;
    private final Requester requester;
    private final ResponseAnalyzer analyzer;

    /**
     * @param serviceConfig Configuration of the monitored service
     */
    public MonitorTask(ServiceConfig serviceConfig) {
        this.config = serviceConfig;
        this.requester = new Requester();
        this.analyzer = new ResponseAnalyzer();
    }

    /**
     * Runs all parts of the service monitoring
     */
    private void runMonitoringTask() {
        ResponseData responseData;
        try {
            responseData = requester.request(config);
        } catch (ConfigException e) {
            logger.error("{} ({})", e.getMessage(), config.getURIAddress());
            return;
        }
        analyzer.analyze(responseData);
    }

    /**
     * Run the task once
     */
    @Override
    public void run() {
        runMonitoringTask();
    }
}




