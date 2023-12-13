import love.forte.simbot.application.Applications;
import love.forte.simbot.core.application.Simple;

/**
 * @author ForteScarlet
 */
public class JAppTests {

    public void app() {

        Applications.launchApplicationAsync(Simple.INSTANCE, configurer -> {
            configurer.config(c -> {

            });
        });

    }

}
