package love.forte.simbot.quantcat.common.keyword

import love.forte.simbot.quantcat.common.filter.MatchType.*
import kotlin.test.*


/**
 *
 * @author ForteScarlet
 */
class KeywordTests {

    @Test
    fun keywordMatchTest() {
        val plainKeyword = SimpleKeyword("Hello", true)
        with(TEXT_EQUALS) {
            assertTrue(match(plainKeyword, "Hello"))
            assertFalse(match(plainKeyword, "Hello1"))
        }

        with(TEXT_EQUALS_IGNORE_CASE) {
            assertTrue(match(plainKeyword, "Hello"))
            assertTrue(match(plainKeyword, "hello"))
            assertFalse(match(plainKeyword, "1Hello"))
        }

        with(TEXT_ENDS_WITH) {
            assertTrue(match(plainKeyword, "Hello"))
            assertTrue(match(plainKeyword, "Forte, Hello"))
            assertFalse(match(plainKeyword, "Hello1"))
        }

        with(TEXT_STARTS_WITH) {
            assertTrue(match(plainKeyword, "Hello"))
            assertTrue(match(plainKeyword, "Hello, forte"))
            assertFalse(match(plainKeyword, "1Hello"))
        }

        with(TEXT_CONTAINS) {
            assertTrue(match(plainKeyword, "Hello"))
            assertTrue(match(plainKeyword, "Hello, forte"))
            assertTrue(match(plainKeyword, "1Hello"))
            assertTrue(match(plainKeyword, "1Hello1"))
            assertTrue(match(plainKeyword, "Hello1"))
        }


        with(REGEX_MATCHES) {
            val regexKeyword = SimpleKeyword("Hello.*", false)
            assertTrue(match(regexKeyword, "Hello"))
            assertTrue(match(regexKeyword, "Hello, forte"))
            assertFalse(match(regexKeyword, "1Hello"))
            assertFalse(match(regexKeyword, "1Hello1"))
            assertTrue(match(regexKeyword, "Hello1"))
        }

        with(REGEX_MATCHES) {
            val regexKeyword = SimpleKeyword("Hello\\d+", false)
            assertFalse(match(regexKeyword, "Hello"))
            assertFalse(match(regexKeyword, "Hello, forte"))
            assertFalse(match(regexKeyword, "1Hello"))
            assertFalse(match(regexKeyword, "1Hello1"))
            assertTrue(match(regexKeyword, "Hello1"))
            assertTrue(match(regexKeyword, "Hello123"))
        }

        with(REGEX_CONTAINS) {
            val regexKeyword = SimpleKeyword("Hello.+", false)
            assertFalse(match(regexKeyword, "Hello"))
            assertTrue(match(regexKeyword, "Hello, forte"))
            assertFalse(match(regexKeyword, "1Hello"))
            assertTrue(match(regexKeyword, "1Hello1"))
            assertTrue(match(regexKeyword, "Hello1"))
        }

        with(REGEX_CONTAINS) {
            val regexKeyword = SimpleKeyword("Hello\\d+", false)
            assertFalse(match(regexKeyword, "Hello"))
            assertFalse(match(regexKeyword, "Hello, forte"))
            assertFalse(match(regexKeyword, "1Hello"))
            assertTrue(match(regexKeyword, "123Hello1"))
            assertTrue(match(regexKeyword, "Hello1"))
            assertTrue(match(regexKeyword, "Hello123"))
        }


    }

    @Test
    fun keywordParamTest() {
        with(SimpleKeyword("Hello, {{name,.+}}!+")) {
            val name = regexValueMatcher.getParam("name", "Hello, forte!!!")
            assertNotNull(name)
            assertNull(regexValueMatcher.getParam("A", "Hello, forte!!!"))
            assertEquals("forte!!", name)
        }
        with(SimpleKeyword("Hello, {{name,.+?}}!+")) {
            val name = regexValueMatcher.getParam("name", "Hello, forte!!!")
            assertNotNull(name)
            assertNull(regexValueMatcher.getParam("A", "Hello, forte!!!"))
            assertEquals("forte", name)
        }
        with(SimpleKeyword("Hello, (?<name>.+)!+")) {
            val name = regexValueMatcher.getParam("name", "Hello, forte!!!")
            assertNotNull(name)
            assertNull(regexValueMatcher.getParam("A", "Hello, forte!!!"))
            assertEquals("forte!!", name)
        }
        with(SimpleKeyword("Hello, (?<name>.+?)!+")) {
            val name = regexValueMatcher.getParam("name", "Hello, forte!!!")
            assertNotNull(name)
            assertNull(regexValueMatcher.getParam("A", "Hello, forte!!!"))
            assertEquals("forte", name)
        }
    }

}
