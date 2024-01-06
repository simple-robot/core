import love.forte.simbot.quantcat.annotations.Filter

/**
 *
 * @author ForteScarlet
 */
class FilterTargetMergeTests {


    @Filter(targets = [Filter.Targets(bots = ["A1", "A2"]), Filter.Targets(bots = ["B1", "B2"], atBot = true)])
    private fun func1() {
    }

    @Filter(targets = [Filter.Targets(bots = ["A3", "A4"]), Filter.Targets(bots = ["B3", "B4"])])
    private fun func2() {
    }

    @Filter(targets = [Filter.Targets()])
    private fun func3() {
    }

    @Filter(targets = [Filter.Targets()])
    private fun func4() {
    }

    @Filter
    @Filter
    fun func5() {
    }

    //
    // @Test
    // fun multiAnnotationTest() {
    //     val javaMethod = ::func5.javaMethod!!
    //
    //     val annotations = javaMethod.getAnnotationsByType(Filter::class.java)
    //     println(annotations.toList())
    //     assertEquals(2, annotations.size)
    // }

}
