package love.forte.simbot.id

import kotlinx.serialization.json.Json
import love.forte.simbot.common.id.*
import love.forte.simbot.common.id.IntID.Companion.ID
import love.forte.simbot.common.id.LongID.Companion.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.UIntID.Companion.ID
import love.forte.simbot.common.id.ULongID.Companion.ID
import love.forte.simbot.common.id.UUID.Companion.UUID
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.random.nextULong
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IDTests {

    @Test
    fun uuidGenerateTest() {
        val id = UUID.random()
        val id2 = id.toString().UUID

        assertEquals(id, id2)
        assertEquals(id as ID, id2.toString().ID as ID)
        assertEquals(id as ID, id2.copy().toString().ID as ID)
        assertTrue(id.equalsExact(id2))
        assertTrue(id.equalsExact(id.copy()))
        assertTrue(id.equalsExact(id2.copy()))
    }

    @Test
    fun equalsTest() {
        assertEquals("1".ID as ID, 1.ID as ID)
    }

    @Test
    fun serializerTest() {
        val json = Json {
            isLenient = true
        }

        val i = Random.nextInt()
        val l = Random.nextLong()
        val ui = Random.nextUInt()
        val ul = Random.nextULong()

        assertEquals(json.encodeToString(IntID.serializer(), i.ID), i.toString())
        assertEquals(json.encodeToString(LongID.serializer(), l.ID), l.toString())
        assertEquals(json.encodeToString(UIntID.serializer(), ui.ID), ui.toString())
        assertEquals(json.encodeToString(ULongID.serializer(), ul.ID), ul.toString())

    }

}
