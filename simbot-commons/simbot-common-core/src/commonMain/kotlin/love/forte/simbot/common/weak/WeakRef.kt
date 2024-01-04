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

/**
 * Create [WeakRef] from [ref].
 */
public expect fun <T : Any> weakRef(ref: T): WeakRef<T>

/**
 * Delegate [WeakRef.value] to a property.
 *
 * ```kotlin
 * val property by weak(value)
 * ```
 */
public operator fun <T : Any> WeakRef<T>.getValue(o: Any?, property: KProperty<*>): T? = value
