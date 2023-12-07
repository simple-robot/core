package love.forte.simbot.suspendrunner


/**
 *
 * @author ForteScarlet
 */
public interface InvokeStrategy<T, R> {

    public operator fun <T1 : T> invoke(block: suspend () -> T1): R

}

