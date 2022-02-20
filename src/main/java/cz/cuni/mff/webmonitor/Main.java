package cz.cuni.mff.webmonitor;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import cz.cuni.mff.webmonitor.config.ConfigException;
import cz.cuni.mff.webmonitor.config.ConfigLoader;
import cz.cuni.mff.webmonitor.config.ServiceConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Class for processing arguments with JCommander library
 */
class Arguments {
    @Parameter(description="configFile")
    List<String> parameters = new ArrayList<>();

    @Parameter(names={"--generateConfig", "-gc"}, description="Generate sample config file")
    boolean generateConfig;

    @Parameter(names="--help", help=true, description="Display all available options")
    boolean help;

    @Parameter(names={"--verbose", "-v"}, description="Log non-error messages")
    boolean verbose;
}

/**
 * Program entry point
 */
public class Main {
    private static final Logger logger = LogManager.getLogger("WebMonitor");

    /**
     * Load configuration from YAML file
     * @param configPath Path to configuration file
     * @return List of monitored services
     */
    public static List<ServiceConfig> loadConfiguration(String configPath) {
        List<ServiceConfig> serviceConfigs = null;
        try {
            serviceConfigs = ConfigLoader.loadFromFile(new FileInputStream(configPath));

        } catch (ConfigException e) {
            logger.error(Messages.messages.getString("CONFIG_ERROR") + ": " + e.getMessage());
            System.exit(1);

        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
            System.exit(1);
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

    /**
     * Set logging to file, the file will be created if it doesn't exist
     * @param path file path
     */
    public static void setLogFile(String path) {
        final LoggerContext context = LoggerContext.getContext(false);
        final Configuration config = context.getConfiguration();
        final PatternLayout layout = PatternLayout.newBuilder()
                .withPattern("%d{yyyy-MM-dd HH:mm:ss} %highlight{%-5level: %msg%n%throwable}")
                .build();
        final Appender appender = FileAppender.newBuilder()
                .setName("FileAppender")
                .withFileName(path)
                .setLayout(layout)
                .build();
        appender.start();
        config.addAppender(appender);

        config.getLoggerConfig("WebMonitor").addAppender(appender, Level.INFO, null);
    }

    /**
     * Entry function
     * @param args
     */
    public static void main(String[] args) {
        Arguments arguments = new Arguments();
        JCommander jct = JCommander.newBuilder()
                .addObject(arguments)
                .build();
        jct.parse(args);

        if (arguments.help) {
            jct.usage();
            return;
        }

        if (!arguments.verbose) {
            Configurator.setLevel(logger.getName(), Level.ERROR);
        }

        if (arguments.generateConfig) {
            InputStream inputStream = Main.class.getResourceAsStream("config-pattern.yaml");
            try {
                assert inputStream != null;
                Files.copy(inputStream, Paths.get("").toAbsolutePath());
            } catch (IOException e) {
                logger.error("%s:\n%s".formatted(Messages.messages.getString("GENERATE_CONF_FAIL"), e.getMessage()));
            }
            System.exit(0);
        }

        // TODO REMOVE for debugging only
        if (arguments.parameters.size() == 0) {
            arguments.parameters = new ArrayList<>(1);
            arguments.parameters.add("examples/config-example.yaml");
            Configurator.setLevel(logger.getName(), Level.INFO);
        }

        if (arguments.parameters.size() != 1) {
            logger.error(Messages.messages.getString("CONFIG_ONE_FILE"));
            System.exit(1);
        }

        // TODO remove for debugging only
        if (Objects.equals(arguments.parameters.get(0), "example")) {
            arguments.parameters.set(0, "examples/config-example.yaml");
        }

        List<ServiceConfig> configuration = loadConfiguration(arguments.parameters.get(0));
        logger.info("Configuration loaded successfully");

        // set logging to file if configured
        if (configuration.get(0).getGlobalConfig().logToFile()) {
            setLogFile(configuration.get(0).getGlobalConfig().getLogFilePath());
            logger.info("Logging to file configured");
        }

        startTasks(configuration);
        logger.info("All tasks started");
    }
}
