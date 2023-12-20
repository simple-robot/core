package love.forte.simbot.common.weak

import java.lang.ref.WeakReference

public actual fun <T : Any> weakRef(ref: T): WeakRef<T> =
    WeakRefImpl(ref)

private class WeakRefImpl<T : Any>(ref: T) : WeakRef<T> {
    @Volatile
    private var ref: WeakReference<T>? = WeakReference(ref)

    override val value: T?
        get() = ref?.let { r ->
            r.get().also { if (it == null) ref = null }
        }

    override fun clear() {
        ref?.clear()
        ref = null
    }
}
