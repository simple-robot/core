package love.forte.simbot.function


// fun interface in JS?

public fun interface Action<in T> {
    public operator fun invoke(value: T)
}
