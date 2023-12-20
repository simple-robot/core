package love.forte.simbot.common.weak

/**
 * 会尝试构建并
 *
 */
public actual fun <T : Any> weakRef(ref: T): WeakRef<T> {
    return runCatching {
        val jsWeakRef = js("new WeakRef(ref);")
        if (jsWeakRef.deref != undefined) { // check deref func.
            JsWeakRefImpl(jsWeakRef)
        } else {
            NonWeakRefImpl(ref)
        }
    }.getOrElse {
        NonWeakRefImpl(ref)
    }
}

private class JsWeakRefImpl<T : Any>(private var weakRef: dynamic /* WeakRef */) : WeakRef<T> {
    override val value: T?
        get() = weakRef?.let { r ->
            (r.deref() as T?).also { if (it == null) weakRef = null }
        } as? T

    override fun clear() {
        weakRef = null
    }
}

private class NonWeakRefImpl<T : Any>(override var value: T?) : WeakRef<T> {
    override fun clear() {
        value = null
    }
}
