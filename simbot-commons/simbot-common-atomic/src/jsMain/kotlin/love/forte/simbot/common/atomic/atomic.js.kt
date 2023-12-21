package love.forte.simbot.common.atomic

/**
 * Create an instance of [AtomicLong]
 */
public actual fun atomic(value: Long): AtomicLong = AtomicLongImpl(value)

private class AtomicLongImpl(override var value: Long) : AtomicLong {
    override fun getAndSet(value: Long): Long {
        val old = this.value
        this.value = value
        return old
    }

    override fun incrementAndGet(delta: Long): Long {
        value += delta
        return value
    }

    override fun decrementAndGet(delta: Long): Long {
        value -= delta
        return value
    }

    override fun getAndIncrement(delta: Long): Long {
        val result = value
        value += delta
        return result
    }

    override fun getAndDecrement(delta: Long): Long {
        val result = value
        value -= delta
        return result
    }

    override fun compareAndSet(expect: Long, value: Long): Boolean {
        val current = this.value
        if (current == expect) {
            this.value = value
            return true
        }

        return false
    }

    override fun compareAndExchange(expect: Long, value: Long): Long {
        val current = this.value
        if (current == expect) {
            this.value = value
        }

        return current
    }

    override fun toString(): String = value.toString()
}

/**
 * Create an instance of [AtomicInt]
 */
public actual fun atomic(value: Int): AtomicInt = AtomicIntImpl(value)

private class AtomicIntImpl(override var value: Int) : AtomicInt {
    override fun getAndSet(value: Int): Int {
        val old = this.value
        this.value = value
        return old
    }

    override fun incrementAndGet(delta: Int): Int {
        value += delta
        return value
    }

    override fun decrementAndGet(delta: Int): Int {
        value -= delta
        return value
    }

    override fun getAndIncrement(delta: Int): Int {
        val result = value
        value += delta
        return result
    }

    override fun getAndDecrement(delta: Int): Int {
        val result = value
        value -= delta
        return result
    }

    override fun compareAndSet(expect: Int, value: Int): Boolean {
        val current = this.value
        if (current == expect) {
            this.value = value
            return true
        }

        return false
    }

    override fun compareAndExchange(expect: Int, value: Int): Int {
        val current = this.value
        if (current == expect) {
            this.value = value
        }

        return current
    }

    override fun toString(): String = value.toString()
}

/**
 * Create an instance of [AtomicInt]
 */
public actual fun atomic(value: UInt): AtomicUInt = AtomicUIntImpl(value)

private class AtomicUIntImpl(override var value: UInt) : AtomicUInt {
    override fun incrementAndGet(delta: UInt): UInt {
        value += delta
        return value
    }

    override fun getAndSet(value: UInt): UInt {
        val current = this.value
        this.value = value
        return current
    }

    override fun decrementAndGet(delta: UInt): UInt {
        value -= delta
        return value
    }

    override fun getAndIncrement(delta: UInt): UInt {
        val result = value
        value += delta
        return result
    }

    override fun getAndDecrement(delta: UInt): UInt {
        val result = value
        value -= delta
        return result
    }

    override fun compareAndSet(expect: UInt, value: UInt): Boolean {
        val current = this.value
        if (current == expect) {
            this.value = value
            return true
        }

        return false
    }

    override fun compareAndExchange(expect: UInt, value: UInt): UInt {
        val current = this.value
        if (current == expect) {
            this.value = value
        }

        return current
    }

    override fun toString(): String = value.toString()
}

/**
 * Create an instance of [AtomicULong]
 */
public actual fun atomic(value: ULong): AtomicULong = AtomicULongImpl(value)

private class AtomicULongImpl(override var value: ULong) : AtomicULong {
    override fun incrementAndGet(delta: ULong): ULong {
        value += delta
        return value
    }

    override fun getAndSet(value: ULong): ULong {
        val current = this.value
        this.value = value
        return current
    }

    override fun decrementAndGet(delta: ULong): ULong {
        value -= delta
        return value
    }

    override fun getAndIncrement(delta: ULong): ULong {
        val result = value
        value += delta
        return result
    }

    override fun getAndDecrement(delta: ULong): ULong {
        val result = value
        value -= delta
        return result
    }

    override fun compareAndSet(expect: ULong, value: ULong): Boolean {
        val current = this.value
        if (current == expect) {
            this.value = value
            return true
        }

        return false
    }

    override fun compareAndExchange(expect: ULong, value: ULong): ULong {
        val current = this.value
        if (current == expect) {
            this.value = value
        }

        return current
    }

    override fun toString(): String = value.toString()
}

/**
 * Create an instance of [AtomicBoolean]
 */
public actual fun atomic(value: Boolean): AtomicBoolean = AtomicBooleanImpl(value)

private class AtomicBooleanImpl(override var value: Boolean) : AtomicBoolean {
    override fun getAndSet(value: Boolean): Boolean {
        val current = this.value
        this.value = value
        return current
    }

    override fun compareAndSet(expect: Boolean, value: Boolean): Boolean {
        val current = this.value
        if (current == expect) {
            this.value = value
            return true
        }

        return false
    }

    override fun compareAndExchange(expect: Boolean, value: Boolean): Boolean {
        val current = this.value
        if (current == expect) {
            this.value = value
        }

        return current
    }

    override fun toString(): String = value.toString()
}

/**
 * Create an instance of [AtomicRef]<[T]>
 */
public actual fun <T> atomicRef(value: T): AtomicRef<T> = AtomicRefImpl(value)

private class AtomicRefImpl<T>(override var value: T) : AtomicRef<T> {
    override fun getAndSet(value: T): T {
        val current = this.value
        this.value = current
        return current
    }

    override fun compareAndSet(expect: T, value: T): Boolean {
        val current = this.value
        if (expect == current) {
            this.value = value
            return true
        }

        return false
    }

    override fun compareAndExchange(expect: T, value: T): T {
        val current = this.value
        if (expect == current) {
            this.value = value
        }

        return current
    }

    override fun toString(): String = value.toString()
}
