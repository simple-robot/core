module simbot.core.springboot.common {
    requires kotlin.stdlib;
    requires static simbot.common.annotations;
    requires transitive simbot.quantcat.annotations;
    requires transitive simbot.quantcat.common;
    requires transitive simbot.core;
    requires static java.annotation;

    exports love.forte.simbot.spring.application;
    exports love.forte.simbot.spring;
}
