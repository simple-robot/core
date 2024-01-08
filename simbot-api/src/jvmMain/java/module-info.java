import love.forte.simbot.component.ComponentFactoryConfigurerProvider;
import love.forte.simbot.component.ComponentFactoryProvider;
import love.forte.simbot.plugin.PluginFactoryConfigurerProvider;
import love.forte.simbot.plugin.PluginFactoryProvider;

module simbot.api {
    requires kotlin.stdlib;
    requires simbot.logger;
    requires static org.jetbrains.annotations;
    requires static simbot.common.annotations;
    requires simbot.common.suspendrunner;
    requires simbot.common.core;
    requires simbot.common.collection;
    requires kotlinx.coroutines.core;
    requires kotlinx.serialization.core;
    requires static kotlinx.coroutines.reactive;
    requires static kotlinx.coroutines.reactor;
    requires static kotlinx.coroutines.rx2;
    requires static kotlinx.coroutines.rx3;
    requires static reactor.core;
    requires static io.reactivex.rxjava2;
    requires static io.reactivex.rxjava3;
    requires static org.reactivestreams;

    // libs.suspend.reversal.annotations?
    exports love.forte.simbot.ability;
    exports love.forte.simbot.application;
    exports love.forte.simbot.bot;
    exports love.forte.simbot.bot.configuration;
    exports love.forte.simbot.component;
    exports love.forte.simbot.definition;
    exports love.forte.simbot.event;
    exports love.forte.simbot.message;
    exports love.forte.simbot.plugin;
    exports love.forte.simbot.resource;

    uses ComponentFactoryProvider;
    uses ComponentFactoryConfigurerProvider;
    uses PluginFactoryProvider;
    uses PluginFactoryConfigurerProvider;

}
