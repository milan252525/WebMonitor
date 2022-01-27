package cz.cuni.mff.webmonitor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        //HTTPRequester req = new HTTPRequester();

        /*
            req.request("https://www.google.com");
            System.out.println();
            req.request("http://laclubs.net/");
            System.out.println();
            req.request("https://laclubs.net/");
            System.out.println();
        */

        try {
            List<Config> configs = Config.loadFromFile(new FileReader("examples/config-example.yaml"));
            for (Config conf : configs) {
                System.out.println(conf.toString());
            }
        } catch (ConfigException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
