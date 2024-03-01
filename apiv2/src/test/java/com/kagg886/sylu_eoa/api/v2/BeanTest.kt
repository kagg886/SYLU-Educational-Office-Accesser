package com.kagg886.sylu_eoa.api.v2

import com.kagg886.sylu_eoa.api.v2.bean.SchoolCalender
import com.kagg886.utils.LoggerReceiver
import com.kagg886.utils.registryLogReceiver
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDate

class BeanTest {
    @Test
    fun testSchoolCalender() {
        val schoolCalender = SchoolCalender(LocalDate.of(2024,2,26),LocalDate.of(2024,7,14))

        Assertions.assertEquals(-1,schoolCalender.currentWeek(LocalDate.of(2024,2,25)))
        Assertions.assertEquals(1,schoolCalender.currentWeek(LocalDate.of(2024,2,26)))
        Assertions.assertEquals(20,schoolCalender.currentWeek(LocalDate.of(2024,7,14)))
        Assertions.assertEquals(2,schoolCalender.currentWeek(LocalDate.of(2024,3,4)))
        Assertions.assertEquals(-1,schoolCalender.currentWeek(LocalDate.of(2024,7,25)))
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun reg() {
            registryLogReceiver(object : LoggerReceiver {
                override fun d(i: String) {
                    println(i)
                }

                override fun i(i: String) {
                    println(i)
                }

                override fun w(i: String) {
                    println(i)
                }

                override fun e(i: String) {
                    System.err.println(i)
                }
            })
        }
    }
}