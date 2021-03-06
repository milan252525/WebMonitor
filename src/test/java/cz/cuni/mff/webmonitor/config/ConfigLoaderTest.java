package cz.cuni.mff.webmonitor.config;

import cz.cuni.mff.webmonitor.notifications.NotifyLevel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

class ConfigLoaderTest {

    @Test
    void testConfigLoading() throws IOException, ConfigException {
        FileInputStream fileInputStream = new FileInputStream("src/test/java/cz/cuni/mff/webmonitor/config/test_config.yaml");

        List<ServiceConfig> testConfig = ConfigLoader.loadFromFile(fileInputStream);

        Assertions.assertEquals(1, testConfig.size());

        ServiceConfig config = testConfig.get(0);

        GlobalConfig global = config.getGlobalConfig();

        Assertions.assertEquals("key", global.getEmailApiKey());
        Assertions.assertEquals("secret", global.getEmailPrivateKey());
        Assertions.assertEquals("webmonitor@webmonitor.com", global.getEmailFrom());
        Assertions.assertEquals("email@example.com", global.getEmailTo());
        Assertions.assertEquals("https://discord.com/api/webhooks/xxx", global.getWebhook());
        Assertions.assertEquals("https://www.example.com", config.getURIAddress().toString());
        Assertions.assertEquals(NotifyLevel.EMAIL, config.getNotifyLevel());
        Assertions.assertEquals(Duration.ofSeconds(2 * 3600 + 30 * 60 + 30), config.getInterval());
        Assertions.assertEquals(Duration.ofSeconds(5), config.getTimeout());
    }

    @Test
    void testStringDurationConversion() {
        Duration d1 = ConfigLoader.strTimeToDuration("5s");
        Duration d2 = ConfigLoader.strTimeToDuration("5m");
        Duration d3 = ConfigLoader.strTimeToDuration("5h");
        Duration d4 = ConfigLoader.strTimeToDuration("5d");
        Duration d5 = ConfigLoader.strTimeToDuration("5d5h5m5s");
        Duration d6 = ConfigLoader.strTimeToDuration("120s");
        Duration d7 = ConfigLoader.strTimeToDuration("");
        Duration d8 = ConfigLoader.strTimeToDuration("5d5d5d");

        Assertions.assertEquals(Duration.ofSeconds(5), d1);
        Assertions.assertEquals(Duration.ofMinutes(5), d2);
        Assertions.assertEquals(Duration.ofHours(5), d3);
        Assertions.assertEquals(Duration.ofDays(5), d4);
        Assertions.assertEquals(Duration.ofSeconds(450305), d5);
        Assertions.assertEquals(Duration.ofSeconds(120), d6);
        Assertions.assertNull(d7);
        Assertions.assertNull(d8);
    }
}