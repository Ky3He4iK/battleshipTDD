package controller

import entity.Map
import controller.Game.GameState.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

internal class GameTest {
    @Test
    fun testStartGameState() {
        val game = Game()
        assertEquals(Prepare, game.state.value)
    }

    @Test
    fun testBotHit(){
        val game = Game()
        while (game.state.value != BotsWin) {
            game.checkGameState()
            val h = game.botHit()
            assertNotEquals(h, Map.HitState.Empty)
        }
        println(game.state.value)
    }

    @Test
    fun restartTest(){
        val game = Game()
        val map1 = game.botsMap
        val map2 = game.playersMap
        game.textState = "123123"
        game.state.value = BotsWin
        game.restart()
        assertNotEquals(map1, game.botsMap)
        assertNotEquals(map2, game.playersMap)
        assertEquals(game.textState, "")
        assertEquals(game.state.value, Prepare)
    }

    @Test
    fun testGameState() {
        val game = Game()
        game.playersMap = Map(false)
        game.checkGameState()
        assertEquals(BotsWin, game.state.value)

        game.playersMap = Map(true)
        game.botsMap = Map(false)
        game.checkGameState()
        assertEquals(PlayersWin, game.state.value)
    }
}