package com.kelvinconnect.discord.command

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RollCommandTest {

    // stateless, so we can reuse a single instance for all tests
    private val cmd: RollCommand = RollCommand()

    @Test
    fun `default roll`() {
        // when no arguments are provided, we roll a d6
        repeat(1_000_000) {
            val result = cmd.computeRoll(arrayOf())
            assertTrue(result.toInt() in 1..6)
        }
    }

    @Test
    fun `roll with variable dice sizes`() {
        for (diceSize in 1 until 100) {
            val rollsPerNumber = IntArray(diceSize) { 0 }
            val totalRolls = 10_000
            repeat(totalRolls) { _ ->
                val response = cmd.computeRoll(arrayOf("$diceSize"))
                response.toInt().let {
                    assertTrue(it in 1..diceSize)
                    rollsPerNumber[it - 1]++
                }
            }
            val zeroRolls = buildList { rollsPerNumber.forEachIndexed { index, i -> if (i == 0) add(index + 1) } }
            assertTrue(zeroRolls.isEmpty(), "Rolled a $diceSize dice $totalRolls times, but never found any $zeroRolls")
        }
    }

    @Test
    fun `roll range`() {
        repeat(1_000_000) {
            val result = cmd.computeRoll(arrayOf("10-20"))
            val intResult = result.toInt()
            assertTrue(intResult in 10..20)
        }
    }

    @Test
    fun `roll many dice at once`() {
        repeat(1_000_000) {
            val result = cmd.computeRoll(arrayOf("10d20"))
            val intResult = result.substring(0, result.indexOf(' '.code.toChar())).toInt()
            assertTrue(intResult >= 20 && intResult <= 10 * 20)
            // (10 - 1) commas means there are 10 comma separated values listed
            assertEquals((result.length - result.replace(",", "").length).toLong(), (10 - 1).toLong())
        }
    }

}
