package love.forte.simbot.common.atomic


/**
 * Atomic [Long].
 * @author ForteScarlet
 */
public interface AtomicLong {
    public var value: Long
    public fun incrementAndGet(delta: Long = 1L): Long
    public fun decrementAndGet(delta: Long = 1L): Long
    public fun getAndIncrement(delta: Long = 1L): Long
    public fun getAndDecrement(delta: Long = 1L): Long
    public fun compareAndSet(expect: Long, value: Long): Boolean
}

/**
 * Atomic [ULong]
 * @author ForteScarlet
 */
public interface AtomicULong {
    public var value: ULong
    public fun incrementAndGet(delta: ULong = 1u): ULong
    public fun decrementAndGet(delta: ULong = 1u): ULong
    public fun getAndIncrement(delta: ULong = 1u): ULong
    public fun getAndDecrement(delta: ULong = 1u): ULong
    public fun compareAndSet(expect: ULong, value: ULong): Boolean
}

/**
 * Atomic [Int].
 * @author ForteScarlet
 */
public interface AtomicInt {
    public var value: Int
    public fun incrementAndGet(delta: Int = 1): Int
    public fun decrementAndGet(delta: Int = 1): Int
    public fun getAndIncrement(delta: Int = 1): Int
    public fun getAndDecrement(delta: Int = 1): Int
    public fun compareAndSet(expect: Int, value: Int): Boolean
}

/**
 * Atomic [UInt]
 * @author ForteScarlet
 */
public interface AtomicUInt {
    public var value: UInt
    public fun incrementAndGet(delta: UInt = 1u): UInt
    public fun decrementAndGet(delta: UInt = 1u): UInt
    public fun getAndIncrement(delta: UInt = 1u): UInt
    public fun getAndDecrement(delta: UInt = 1u): UInt
    public fun compareAndSet(expect: UInt, value: UInt): Boolean
}

/**
 * Atomic [Boolean]
 * @author ForteScarlet
 */
public interface AtomicBoolean {
    public var value: Boolean
    public fun compareAndSet(expect: Boolean, value: Boolean): Boolean
}

public interface AtomicRef<T> {
    public var value: T
}

// public expect fun atomic(value: Long): AtomicLong
// public expect fun atomic(value: Int): AtomicInt
// public expect fun atomic(value: UInt): AtomicInt
// public expect fun atomic(value: Boolean): AtomicBoolean
// public expect fun atomic(value: ULong): AtomicULong

// public fun atomicUL(value: ULong): AtomicULong = atomic(value)
