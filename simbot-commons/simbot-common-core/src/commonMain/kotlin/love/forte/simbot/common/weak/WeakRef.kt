package love.forte.simbot.common.weak

import kotlin.reflect.KProperty


/**
 * A weak reference definition.
 *
 * @author ForteScarlet
 */
public interface WeakRef<T : Any> {
    public val value: T?
    public fun clear()
}

public expect fun <T : Any> weakRef(ref: T): WeakRef<T>

// @Suppress("nothing_to_inline")
public inline operator fun <T : Any> WeakRef<T>.getValue(o: Any?, property: KProperty<*>): T? = value
