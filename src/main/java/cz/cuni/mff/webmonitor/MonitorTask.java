package cz.cuni.mff.webmonitor;

import cz.cuni.mff.webmonitor.config.ServiceConfig;

/**
 * Class representing a single monitored service
 */
public class MonitorTask implements Runnable {

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
        ResponseData responseData = requester.request(config);
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




