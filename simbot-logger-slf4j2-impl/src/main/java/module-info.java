import love.forte.simbot.logger.slf4j2.SimbotLoggerProvider;
import org.slf4j.spi.SLF4JServiceProvider;

module simbot.logger.slf4j2impl {
    requires kotlin.stdlib;
    requires simbot.logger;
    requires com.lmax.disruptor;

    exports love.forte.simbot.logger.slf4j2;
    exports love.forte.simbot.logger.slf4j2.color;
    exports love.forte.simbot.logger.slf4j2.dispatcher;

    provides SLF4JServiceProvider with SimbotLoggerProvider;
}