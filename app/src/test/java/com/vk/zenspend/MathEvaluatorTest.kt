package com.vk.zenspend

import com.vk.zenspend.domain.util.MathEvaluator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MathEvaluatorTest {

    @Test
    fun testSimpleAdditionAndMultiplication() {
        val res = MathEvaluator.evaluate("120 + 35 * 2")
        assertNotNull(res)
        assertEquals(190.0, res!!, 0.001)
    }

    @Test
    fun testDecimalAndDivision() {
        val res = MathEvaluator.evaluate("50 / 2 + 12.5")
        assertNotNull(res)
        assertEquals(37.5, res!!, 0.001)
    }

    @Test
    fun testSingleNumber() {
        val res = MathEvaluator.evaluate("42.99")
        assertNotNull(res)
        assertEquals(42.99, res!!, 0.001)
    }
}
