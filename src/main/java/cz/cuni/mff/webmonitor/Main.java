package cz.cuni.mff.webmonitor;

import cz.cuni.mff.webmonitor.config.ServiceConfig;
import cz.cuni.mff.webmonitor.config.ConfigException;
import cz.cuni.mff.webmonitor.config.ConfigLoader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Requester req = new Requester();

        try {
            List<ServiceConfig> serviceConfigs = ConfigLoader.loadFromFile(new FileInputStream("examples/config-example.yaml"));
            for (ServiceConfig conf : serviceConfigs) {
                System.out.println(conf.toString());
                ResponseData rd = req.request(conf);
                if (rd.wasSuccess()) {
                    System.out.println(rd.getResponse().toString());
                } else {
                    System.out.println(rd.getException().toString());
                }
            }
        } catch (ConfigException e) {
            System.err.println(Messages.messages.getString("CONFIG_ERROR") + ": " + e.getMessage());
            System.exit(1);
        }
    }
}
