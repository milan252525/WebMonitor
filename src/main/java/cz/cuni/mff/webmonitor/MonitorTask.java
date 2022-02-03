package cz.cuni.mff.webmonitor;

import cz.cuni.mff.webmonitor.config.ServiceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MonitorTask implements Runnable {

    private static final Logger logger = LogManager.getLogger("WebMonitor");

    private final ServiceConfig config;
    private final Requester requester;
    private final ResponseAnalyzer analyzer;

    public MonitorTask(ServiceConfig serviceConfig) {
        this.config = serviceConfig;
        this.requester = new Requester();
        this.analyzer = new ResponseAnalyzer();
    }

    private void runMonitoringTask() {
        ResponseData responseData = requester.request(config);
        analyzer.analyze(responseData);
    }

    @Override
    public void run() {
        runMonitoringTask();
    }
}




