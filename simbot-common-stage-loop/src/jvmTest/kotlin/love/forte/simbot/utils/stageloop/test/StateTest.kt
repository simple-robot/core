package love.forte.simbot.utils.stageloop.test

import love.forte.simbot.utils.stageloop.State
import love.forte.simbot.utils.stageloop.loop


suspend fun main() {
    MyState.Start.loop { it.also(::println) }.also(::println)
}

sealed class MyState : State<MyState>() {

    /** 一个用于启动状态循环的状态 */
    object Start : MyState() {
        override suspend fun invoke(): MyState = Process(0)
    }

    /** 中间的处理状态，可以直接隐藏 */
    private data class Process(val value: Int) : MyState() {
        override suspend fun invoke(): MyState? {
            return if (value < 10) Process(value + 1) else End(value)
        }
    }

    /** 最后一个状态，会被 `State.loop()` 返回 */
    data class End(val value: Int): MyState() {
        override suspend fun invoke(): MyState? = null
    }
}
