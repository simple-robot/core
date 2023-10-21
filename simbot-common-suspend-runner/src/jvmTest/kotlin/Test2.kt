import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


fun main() {
    Executors.newCachedThreadPool()

    val handle = MethodHandles.publicLookup().findStatic(Executors::class.java, "newCachedThreadPool", MethodType.methodType(ExecutorService::class.java))
    val e = handle.invoke() as ExecutorService
    println(e)
}
