module simbot.common.suspendrunner {
    requires kotlin.stdlib;
    requires kotlinx.coroutines.core;
    requires static simbot.common.annotations;
    requires simbot.logger;

    exports love.forte.simbot.suspendrunner;
}
