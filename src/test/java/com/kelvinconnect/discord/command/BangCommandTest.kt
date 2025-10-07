package com.kelvinconnect.discord.command

import com.kelvinconnect.discord.util.asOrdinal
import org.junit.Test
import kotlin.test.DefaultAsserter.assertEquals

class BangCommandTest {

    @Test
    fun `test bang command`() {
        val lyrics = listOf(
            "He shot me down",
            "I hit the ground",
            "That awful sound",
            "My baby shot me down",
            "I shot you down",
            "You hit the ground",
            "That awful sound",
            "I used to shoot you down",
            "",
        )
        val command = BangCommand()
        (0..100).forEach { i ->
            val message = command.onBang(arrayOf("ignore", "contents"))
            val expectedLyric = lyrics[i % lyrics.size]
            assertEquals("${i.asOrdinal()} response", expectedLyric, message)
        }
    }

}