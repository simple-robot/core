package love.forte.simbot.function


// fun interface in JS?

/**
 * Represents an action that can be performed on a value of type T.
 *
 * @param T the type of the value that the action operates on
 */
public fun interface Action<in T> {
    /**
     * Invokes the operator function with the given value.
     *
     * @param value the value to be used in the operator function
     */
    public operator fun invoke(value: T)
}
