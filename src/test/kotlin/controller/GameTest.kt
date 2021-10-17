package controller

import Game
import Game.GameState.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class GameTest {
    @Test
    fun testStartGameState() {
        val game = Game()
        assertEquals(Prepare, game.state.value)
    }
}