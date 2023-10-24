package love.forte.simbot.event

import kotlinx.coroutines.Deferred
import love.forte.simbot.event.StandardEventResult.Invalid.isTruncated
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic


/**
 * 一个事件（处理）结果。
 *
 * 通过 [EventListener.handle] 响应得到，用于表示此次事件处理的结果内容。
 *
 * ### Standard Event Result
 *
 * [StandardEventResult] 定义了一些特殊的、具有含义的类型实现。
 * 它们可能会被特殊处理或用作一些逻辑判断。
 *
 * @author ForteScarlet
 */
public interface EventResult {
    /**
     * 此事件处理的结果体。可以是任何内容，或者是一个空。
     */
    public val content: Any?

    /**
     * 是否阻止下一个事件处理逻辑的执行。
     *
     * 这只会截断顺序执行的函数，而不会影响异步函数，
     * 异步函数也无法通过 [isTruncated] 对后续函数进行截断。
     */
    public val isTruncated: Boolean


    public companion object {
        /**
         * 得到一个代表无效的结果。
         *
         * [invalid] 通常用于表示事件不匹配、逻辑不匹配等情况。
         *
         * 如果你的监听函数正常执行，只是没有可用的结果或无需提供结果，
         * 那么推荐使用 [empty]。
         *
         * @see StandardEventResult.Invalid
         * @see StandardEventResult.Empty
         */
        @get:JvmStatic
        @get:JvmName("invalid")
        public val invalid: EventResult get() = StandardEventResult.Invalid

        /**
         * 得到一个代表空内容的结果。
         *
         * [empty] 通常用于表示事件处理正常、但是无可用结果或无需提供结果时。
         *
         * 如果监听函数内对本次事件处理无效（例如类型不匹配、逻辑不匹配等），推荐使用 [invalid]。
         *
         * @see StandardEventResult.Empty
         */
        @JvmStatic
        @JvmOverloads
        public fun empty(isTruncated: Boolean = false): EventResult = StandardEventResult.Empty.instance(isTruncated)

        /**
         * 通过参数构建得到一个对应的 [EventResult] 实例。
         *
         * @param content 处理结果内容
         * @param isTruncated 是否截断后续处理逻辑。默认为 `false`
         */
        @JvmStatic
        @JvmOverloads
        public fun of(content: Any?, isTruncated: Boolean = false): EventResult {
            if (content != null) return StandardEventResult.Simple(content, isTruncated)
            return empty(isTruncated)
        }
    }
}

/**
 * [EventResult.content] 是否为空。
 */
public inline val EventResult.isEmpty: Boolean get() = content == null

/**
 * [EventResult] 的标准实现。大部分有用的类型、
 * 具有特殊作用的类型和可扩展的类型都在 [StandardEventResult] 下。
 *
 * @see EventResult
 */
public sealed class StandardEventResult : EventResult {

    /**
     * 一些 [content] 始终为 `null` 的 [StandardEventResult] 实现。
     */
    public sealed class EmptyResult : StandardEventResult()

    /**
     * 无效的结果。当响应 [Invalid] 时代表此事件对于本次处理来说是无效的。
     * 可用于事件类型不匹配、逻辑判断未通过等情况。
     *
     * 如果你的事件逻辑正常执行了，只是没有什么可传递的结果，那么更建议使用 [Empty] 而不是 [Invalid]。
     *
     * @see Empty
     */
    public data object Invalid : EmptyResult() {
        override val content: Any? get() = null
        override val isTruncated: Boolean get() = false
    }

    /**
     * 空结果。当响应 [Empty] 时代表此事件对于本次处理来说是正常处理的，
     * 但是没有可用的响应结果或无需提供结果。
     *
     * 如果你的事件逻辑并没有正常执行，
     * 例如逻辑匹配未通过、或事件类型不匹配等，那么更建议使用 [Invalid] 而不是 [Empty]。
     *
     * @property isTruncated 是否截断后续逻辑的执行
     * @see Invalid
     */
    public class Empty private constructor(override val isTruncated: Boolean) : EmptyResult() {
        override val content: Any? get() = null

        public companion object {
            private val Truncated = Empty(true)
            private val NoTruncated = Empty(false)

            /**
             * 根据 [isTruncated] 获取一个 [Empty] 实例。
             *
             * @param isTruncated 是否截断后续逻辑的执行
             */
            @JvmStatic
            @JvmOverloads
            public fun instance(isTruncated: Boolean = false): Empty = if (isTruncated) Truncated else NoTruncated

            /**
             * 获取一个始终截断后续逻辑的 [Empty] 实例。
             */
            @JvmStatic
            public fun truncated(): Empty = Truncated
        }
    }

    /**
     * 用于表示一个异步任务的结果。
     */
    public abstract class Async : StandardEventResult() {
        /**
         * 用来表示一个异步任务的 [content], 例如 [Deferred] 或 [Future].
         */
        abstract override val content: Any?

        /**
         * 是否阻止下一个事件处理逻辑的执行。
         * 默认情况下，异步任务将不会阻断后续事件的执行。
         */
        override val isTruncated: Boolean get() = false

        /**
         * 等待异步任务的结果。
         *
         * 异步任务的结果应当仍然是一个 [EventResult]，且此结果通常应当不再是 [Async] 类型。
         */
        @JvmSynthetic
        public abstract suspend fun awaitContent(): EventResult
    }

    /**
     * 代表 [content] 可能为一个反应式的结果，并且允许其在一个函数结束时进行收集。
     */
    public abstract class CollectableReactively : StandardEventResult() {

        /**
         *
         * 当 [content] 的返回值为 _reactive api_ 相关或异步结果相关的内容，且当前 [EventResult] 实例为 **[CollectableReactively]** 类型的时候，
         * 处理器应当对这类相关的api进行收集。这通常使用在Java使用者或者与其他reactive API配合使用的时候。
         *
         * 比如当你的函数返回了 [flux](https://projectreactor.io/docs/core/3.4.1/api/reactor/core/publisher/Flux.html),
         * 那么它将会被收集为 [List] 后重新作为 [content] 并通过 [collected] 构建为一个新的 [EventResult].
         * 同样的，如果你返回的是 [kotlinx.coroutines.flow.Flow], 也会在函数返回后进行收集。
         *
         * 收集行为会在返回值返回后(某个监听函数处理结束后, 下一个监听函数开始执行前)立即执行, 而不是等待所有监听函数执行结束后。
         *
         * 支持的收集类型有：
         * - `java.util.concurrent.CompletionStage` (`java.util.concurrent.CompletableFuture`) (JVM)
         * - [kotlinx.coroutines.Deferred] (不支持 [kotlinx.coroutines.Job])
         * - [kotlinx.coroutines.flow.Flow]
         * - `kotlin.js.Promise` (JS)
         * - `org.reactivestreams.Publisher` (JVM)
         * - `reactor.core.publisher.Flux` (JVM)
         * - `reactor.core.publisher.Mono` (JVM)
         * - `io.reactivex.CompletableSource` (JVM)
         * - `io.reactivex.SingleSource` (JVM)
         * - `io.reactivex.MaybeSource` (JVM)
         * - `io.reactivex.ObservableSource` (JVM)
         * - `io.reactivex.Flowable` (JVM)
         * - `io.reactivex.rxjava3.core.CompletableSource` (JVM)
         * - `io.reactivex.rxjava3.core.SingleSource` (JVM)
         * - `io.reactivex.rxjava3.core.MaybeSource` (JVM)
         * - `io.reactivex.rxjava3.core.ObservableSource` (JVM)
         * - `io.reactivex.rxjava3.core.Flowable` (JVM)
         *
         * 其他详情请见 [kotlinx-coroutines-reactive](https://github.com/Kotlin/kotlinx.coroutines/blob/master/reactive/README.md) .
         *
         * 当响应式结果被收集后，后续流转的 [EventResult] 可能会与当前类型不同。更多参考 [collected]。
         */
        abstract override val content: Any?

        /**
         * 当响应式结果 [content] 被收集完毕后通过 [collected] 提供其收集的结果 [collectedContent],
         * 并作为一个新的 [EventResult] 结果提供.
         *
         * [collected] 的结果不会再被二次收集, 因此假若 [collectedContent] 仍然为响应式类型, 则它们将会被忽略并直接作为结果返回.
         *
         * [collected] 的结果可能会与当前类型不同（例如 [Simple] 中的结果收集完成后可能会被作为一个 [Empty] 进行流转）。
         *
         */
        @JvmSynthetic
        public abstract fun collected(collectedContent: Any?): EventResult
    }

    /**
     * 一个对 [EventResult] 内容进行简单实现的 [StandardEventResult] 数据类型。
     * 没有特殊含义，即为简单包装。
     *
     * 建议通过工厂函数 [EventResult.of] 来间接获取 [Simple] 并在可能的情况下使用其他选项来减少对象实例的构建。
     *
     */
    public data class Simple(override val content: Any?, override val isTruncated: Boolean) :
        StandardEventResult() // TODO CollectableReactively

}
