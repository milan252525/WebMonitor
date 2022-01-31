package cz.cuni.mff.webmonitor;

import cz.cuni.mff.webmonitor.config.ServiceConfig;
import cz.cuni.mff.webmonitor.config.ConfigException;
import cz.cuni.mff.webmonitor.config.ConfigLoader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        HTTPRequester req = new HTTPRequester();

        /*
            req.request("https://www.google.com");
            System.out.println();
            req.request("http://laclubs.net/");
            System.out.println();
            req.request("https://laclubs.net/");
            System.out.println();
        */

        try {
            List<ServiceConfig> serviceConfigs = ConfigLoader.loadFromFile(new FileInputStream("examples/config-example.yaml"));
            for (ServiceConfig conf : serviceConfigs) {
                System.out.println(conf.toString());
                req.request(conf.getWebAddress());
            }
        } catch (ConfigException e) {
            System.err.println(Messages.messages.getString("CONFIG_ERROR") + ": " + e.getMessage());
            System.exit(1);
        }
    }
}
