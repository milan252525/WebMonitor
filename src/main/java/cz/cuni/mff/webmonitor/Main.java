package cz.cuni.mff.webmonitor;

import cz.cuni.mff.webmonitor.config.ServiceConfig;
import cz.cuni.mff.webmonitor.config.ConfigException;
import cz.cuni.mff.webmonitor.config.ConfigLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger = LogManager.getLogger("WebMonitor");

    /**
     * Load configuration from YAML file
     * @return List of monitored services
     */
    public static List<ServiceConfig> loadConfiguration() {
        List<ServiceConfig> serviceConfigs = null;
        try {
            serviceConfigs = ConfigLoader.loadFromFile(new FileInputStream("examples/config-example.yaml"));

        } catch (ConfigException e) {
            logger.error(Messages.messages.getString("CONFIG_ERROR") + ": " + e.getMessage());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return serviceConfigs;
    }

    /**
     * Create a ScheduledThreadPoolExecutor of monitoring tasks.
     * Adds shutdown hook to kill all tasks
     * @param serviceConfigs List of configurations
     */
    public static void startTasks(List<ServiceConfig> serviceConfigs) {
        ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(serviceConfigs.size());

        for (ServiceConfig config : serviceConfigs) {
            Runnable task = new MonitorTask(config);
            threadPool.scheduleAtFixedRate(task,0, config.getInterval().getSeconds(), TimeUnit.SECONDS);
        }

        // safely shutdown all threads on application exit
        Runtime.getRuntime().addShutdownHook(new Thread(threadPool::shutdown));
    }

    public static void main(String[] args) {
        List<ServiceConfig> configuration = loadConfiguration();
        logger.info("Configuration loaded successfully");

        startTasks(configuration);
        logger.info("All tasks started");
    }
}
