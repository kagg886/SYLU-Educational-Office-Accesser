package com.kagg886.sylu_eoa

import com.kagg886.sylu_eoa.api.v2.bean.ClassUnit
import com.kagg886.sylu_eoa.util.DESCrypt
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalTime
import java.util.UUID

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun testDES() {
        val crypt = DESCrypt(UUID.randomUUID().toString())

        println(crypt.encrypt("qwq"))
    }

    @Test
    fun testFlow() = runBlocking {
        val flow = flow {
            delay(10000)
            emit("qwq")
        }
        println(withTimeoutOrNull(1000) {
            flow.last()
        })
    }
    @Test
    fun testConvertStatus() {
        //1-2节为8:00-9:40
        val a = ClassUnit("111", "222", "333", "3-8周", "1-2", "1", "1.0", "考试课")
        assertEquals(ClassType.WAIT, getTypeInClass(a, LocalTime.of(7, 0)))
        assertEquals(ClassType.PROCESS, getTypeInClass(a, LocalTime.of(8, 20)))
        assertEquals(ClassType.SUCCESS, getTypeInClass(a, LocalTime.of(9, 50)))

        //5-6节为13:00-14:40
        val b = ClassUnit("111", "222", "333", "3-8周", "5-6", "1", "1.0", "考试课")
        assertEquals(ClassType.WAIT, getTypeInClass(b, LocalTime.of(12, 59)))
        assertEquals(ClassType.PROCESS, getTypeInClass(b, LocalTime.of(13, 30)))
        assertEquals(ClassType.SUCCESS, getTypeInClass(b, LocalTime.of(14, 45)))
    }
}

private fun getTypeInClass(u: ClassUnit, now: LocalTime = LocalTime.now()): ClassType {
    val dt = (u.rangeEveryDay[0] + 1) / 2 //1-2 3-4 5-6 7-8 9-10 11-12
    val (start, end) = when (dt) {
        1 -> LocalTime.of(8, 0) to LocalTime.of(9, 40)
        2 -> LocalTime.of(10, 0) to LocalTime.of(11, 40)
        3 -> LocalTime.of(13, 0) to LocalTime.of(14, 40)
        4 -> LocalTime.of(15, 0) to LocalTime.of(16, 40)
        5 -> LocalTime.of(17, 0) to LocalTime.of(18, 40)
        6 -> LocalTime.of(19, 0) to LocalTime.of(20, 40)


        else -> throw IllegalStateException("no this class")
    }


    return when {
        now < start -> ClassType.WAIT
        now > end -> ClassType.SUCCESS
        else -> ClassType.PROCESS
    }
}

enum class ClassType {
    WAIT, PROCESS, SUCCESS
}