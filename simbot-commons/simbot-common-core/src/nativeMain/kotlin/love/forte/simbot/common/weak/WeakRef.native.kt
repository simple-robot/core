package love.forte.simbot.common.weak

import kotlin.concurrent.Volatile
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.WeakReference

@OptIn(ExperimentalNativeApi::class)
public actual fun <T : Any> weakRef(ref: T): WeakRef<T> =
    WeakRefImpl(ref)

@ExperimentalNativeApi
private class WeakRefImpl<T : Any>(ref: T) : WeakRef<T> {
    @Volatile
    private var ref: WeakReference<T>? = WeakReference(ref)

    override val value: T?
        get() = ref?.let { r ->
            r.value.also { if (it == null) ref = null }
        }

    override fun clear() {
        ref?.clear()
        ref = null
    }
}
