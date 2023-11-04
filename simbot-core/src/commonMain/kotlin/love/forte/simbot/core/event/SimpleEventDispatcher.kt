package love.forte.simbot.core.event

import love.forte.simbot.event.EventDispatcher
import love.forte.simbot.event.EventDispatcherConfiguration
import love.forte.simbot.event.EventInterceptorRegistrationProperties
import love.forte.simbot.event.EventListenerRegistrationProperties

// TODO


public interface SimpleEventDispatcher : EventDispatcher

// TODO

public typealias SimpleLP = SimpleEventListenerRegistrationProperties
public typealias SimpleIP = SimpleEventInterceptorRegistrationProperties

public interface SimpleEventListenerRegistrationProperties : EventListenerRegistrationProperties

public interface SimpleEventInterceptorRegistrationProperties : EventInterceptorRegistrationProperties


public interface SimpleEventDispatcherConfiguration : EventDispatcherConfiguration

