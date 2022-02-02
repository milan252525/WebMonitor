package cz.cuni.mff.webmonitor;

import cz.cuni.mff.webmonitor.config.ServiceConfig;
import cz.cuni.mff.webmonitor.config.ConfigException;
import cz.cuni.mff.webmonitor.config.ConfigLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class Main {
    private static final Logger logger = LogManager.getLogger("WebMonitor");

    public static void main(String[] args) throws FileNotFoundException {
        Requester requester = new Requester();
        ResponseAnalyzer analyzer = new ResponseAnalyzer();

        try {
            List<ServiceConfig> serviceConfigs = ConfigLoader.loadFromFile(new FileInputStream("examples/config-example.yaml"));

            logger.info("Configuration loaded successfully");

            for (ServiceConfig config : serviceConfigs) {

                //logger.info("[{}] requesting", config.getURIAddress());

                ResponseData responseData = requester.request(config);
                analyzer.analyze(responseData);
            }
        } catch (ConfigException e) {
            System.err.println(Messages.messages.getString("CONFIG_ERROR") + ": " + e.getMessage());
            System.exit(1);
        }
    }
}
