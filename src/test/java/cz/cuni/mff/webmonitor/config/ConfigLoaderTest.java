package cz.cuni.mff.webmonitor.config;

import cz.cuni.mff.webmonitor.NotifyLevel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

class ConfigLoaderTest {

    @Test
    void loadFromFile() throws IOException, ConfigException {
        FileInputStream fileInputStream = new FileInputStream("src/test/java/cz/cuni/mff/webmonitor/config/test_config.yaml");

        List<ServiceConfig> testConfig = ConfigLoader.loadFromFile(fileInputStream);

        Assertions.assertEquals(1, testConfig.size());

        ServiceConfig config = testConfig.get(0);

        Assertions.assertEquals("email@example.com", config.getEmail());
        Assertions.assertEquals("https://www.example.com", config.getURIAddress().toString());
        Assertions.assertEquals(NotifyLevel.EMAIL, config.getNotifyLevel());
        Assertions.assertEquals(Duration.ofSeconds(2 * 3600 + 30 * 60 + 30), config.getInterval());
        Assertions.assertEquals(Duration.ofSeconds(5), config.getTimeout());
        Assertions.assertEquals("example.log", config.getLogFile());
    }
}