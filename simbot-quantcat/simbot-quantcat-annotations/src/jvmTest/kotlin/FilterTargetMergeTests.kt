import love.forte.simbot.quantcat.annotations.EmptyTargets
import love.forte.simbot.quantcat.annotations.Filter
import love.forte.simbot.quantcat.annotations.mergeTargets
import love.forte.simbot.quantcat.annotations.plus
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaMethod
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 *
 * @author ForteScarlet
 */
class FilterTargetMergeTests {


    @Filter(targets = [Filter.Targets(bots = ["A1", "A2"]), Filter.Targets(bots = ["B1", "B2"], atBot = true)])
    private fun func1(){}

    @Filter(targets = [Filter.Targets(bots = ["A3", "A4"]), Filter.Targets(bots = ["B3", "B4"])])
    private fun func2(){}

    @Filter(targets = [Filter.Targets()])
    private fun func3(){}

    @Filter(targets = [Filter.Targets()])
    private fun func4(){}

    @Filter
    @Filter
    fun func5(){}

    @Test
    fun mergeTest() {
        val f1 = ::func1
        val f2 = ::func2

        val f1t = f1.findAnnotation<Filter>()!!.targets
        val f2t = f2.findAnnotation<Filter>()!!.targets

        assertEquals(
            (::func3.findAnnotation<Filter>()!!.targets.first() + ::func4.findAnnotation<Filter>()!!.targets.first()),  EmptyTargets
        )

        val mergedTarget = mergeTargets(buildList {
            addAll(f1t)
            addAll(f2t)
        })

        assertTrue(mergedTarget.atBot)
        assertEquals(
            setOf("A1", "A2", "A3", "A4", "B1", "B2", "B3", "B4"),
            mergedTarget.bots.toSet()
        )
    }


    @Test
    fun multiAnnotationTest() {
        val javaMethod = ::func5.javaMethod!!

        val annotations = javaMethod.getAnnotationsByType(Filter::class.java)
        println(annotations.toList())
        assertEquals(2, annotations.size)
    }

}
