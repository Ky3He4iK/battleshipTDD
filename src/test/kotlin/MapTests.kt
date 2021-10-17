import entity.Boat
import entity.Map

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.powermock.reflect.Whitebox

class MapTests {
    @Test
    fun testCreateEmptyMap() {
        val map = Map(false)
        assertEquals(10, map.cells.size, "Map size X")
        map.cells.forEach { cells_row ->
            assertEquals(10, cells_row.size, "Map size Y")
            cells_row.forEach {
                assertFalse(it.isBoatCell, "isBoatCell")
                assertFalse(it.isHitCell, "isHitCell")
            }
        }
    }

    @Test
    fun testCreateFilledMap() {
        val map = Map(true)
        val boats: ArrayList<Boat> = Whitebox.getInternalState(map, "boats")
        assertEquals(10, boats.size)
        assertEquals(listOf(1, 1, 1, 1, 2, 2, 2, 3, 3, 4), boats.map { boat -> boat.coords.size }.sorted())
        val busy_cells = arrayListOf<Pair<Int, Int>>()
        boats.forEach { boat ->
            var cx = boat.coords[0].first
            var cy = boat.coords[0].second
            var dirHorizontal: Boolean? = null
            boat.coords.forEach { coord ->
                busy_cells.add(coord)
                when (dirHorizontal) {
                    true -> {
                        assertEquals(cx + 1, coord.first, "boat horizontal X")
                        cx++
                        assertEquals(cy, coord.second, "boat horizontal Y")
                    }
                    false ->  {
                        assertEquals(cx, coord.first, "boat vertical X")
                        assertEquals(cy + 1, coord.second, "boat vertical Y")
                        cy++
                    }
                    null -> {
                        if (cx != coord.first) {
                            dirHorizontal = true
                            cx++
                        }
                        else if (cy != coord.second) {
                            dirHorizontal = false
                            cy++
                        }
                    }
                }
                assertTrue(coord.first in 0 until 10, "boat X in bounds")
                assertTrue(coord.second in 0 until 10, "boat Y in bounds")
                assertTrue(map.cells[coord.first][coord.second].isBoatCell, "isBoatCell")
            }
        }
        assertEquals(10, map.cells.size, "Map size X")
        map.cells.forEachIndexed { x, cells_row ->
            assertEquals(10, cells_row.size, "Map size Y")
            cells_row.forEachIndexed { y, cell ->
                if (Pair(x, y) !in busy_cells)
                    assertFalse(cell.isBoatCell, "isBoatCell")
                assertFalse(cell.isHitCell, "isHitCell")
            }
        }
    }

    @Test
    fun testSetBoat() {
        val map = Map(false)
        assertTrue(map.setBoat(6, 9, 4, true))
        val boats: ArrayList<Boat> = Whitebox.getInternalState(map, "boats")

        assertEquals(arrayListOf(Boat(ArrayList(List(4) { Pair(it + 6, 9) }))), boats)
        map.cells.forEachIndexed { cx, cells_row ->
            cells_row.forEachIndexed { cy, cell ->
                assertEquals(cx in 6 until 10 && cy == 9, cell.isBoatCell, "isBoatCell")
                assertFalse(cell.isHitCell, "isHitCell")
            }
        }
    }

    @Test
    fun testSetBoatVertical() {
        val map = Map(false)
        assertTrue(map.setBoat(9, 6, 4, false))
        val boats: ArrayList<Boat> = Whitebox.getInternalState(map, "boats")
        assertEquals(arrayListOf(Boat(ArrayList(List(4) { Pair(9, it + 6) }))), boats)
        map.cells.forEachIndexed { cx, cells_row ->
            cells_row.forEachIndexed { cy, cell ->
                assertEquals(cx == 9 && cy in 6 until 10, cell.isBoatCell, "isBoatCell")
                assertFalse(cell.isHitCell, "isHitCell")
            }
        }
    }

    @Test
    fun testSetBoatOutOfBounds() {
        val map = Map(false)
        assertFalse(map.setBoat(7, 9, 4, false))
        val boats: ArrayList<Boat> = Whitebox.getInternalState(map, "boats")
        assertTrue(boats.isEmpty())
        map.cells.forEachIndexed { _, cells_row ->
            cells_row.forEachIndexed { _, cell ->
                assertFalse(cell.isBoatCell, "isBoatCell")
                assertFalse(cell.isHitCell, "isHitCell")
            }
        }
    }

    @Test
    fun testSetBoatBusy() {
        val map = Map(false)
        assertTrue(map.setBoat(6, 9, 4, true))
        assertFalse(map.setBoat(5, 9, 1, true))
        val boats: ArrayList<Boat> = Whitebox.getInternalState(map, "boats")
        assertEquals(arrayListOf(Boat(ArrayList(List(4) { Pair(it + 6, 9) }))), boats)
        map.cells.forEachIndexed { cx, cells_row ->
            cells_row.forEachIndexed { cy, cell ->
                assertEquals(cx in 6 until 10 && cy == 9, cell.isBoatCell, "isBoatCell")
                assertFalse(cell.isHitCell, "isHitCell")
            }
        }
    }

    @Test
    fun testHasNoCollisions() {
        val positive = arrayOf(Pair(0, 0), Pair(9, 7), Pair(5, 3), Pair(7, 6), Pair(4, 9))
        val negative = arrayOf(Pair(-1, -1), Pair(10, 10), Pair(6, 9), Pair(9, 9), Pair(9, 8))
        val map = Map(false)
        map.setBoat(6, 9, 4, true)
        positive.forEach {
            assertTrue(Whitebox.invokeMethod(map, "hasNoCollisions", it.first, it.second), "${it.first} ${it.second}")
        }
        negative.forEach {
            assertFalse(Whitebox.invokeMethod(map, "hasNoCollisions", it.first, it.second), "${it.first} ${it.second}")
        }
    }

    @Test
    fun testIsEmptyOrOutOfMap() {
        val positive = arrayOf(Pair(0, 0), Pair(9, 8), Pair(5, 3), Pair(8, 6), Pair(5, 9), Pair(-1, -1), Pair(10, 10))
        val negative = Array(4) { Pair(it + 6, 9) }
        val map = Map(false)
        map.setBoat(6, 9, 4, true)
        positive.forEach {
            assertTrue(Whitebox.invokeMethod(map, "isEmptyOrOutOfMap", it.first, it.second), "${it.first} ${it.second}")
        }
        negative.forEach {
            assertFalse(Whitebox.invokeMethod(map, "isEmptyOrOutOfMap", it.first, it.second), "${it.first} ${it.second}")
        }
    }

    @Test
    fun testHit() {
        val testData = arrayOf(
            Pair(Pair(0, 0), Map.HitState.Miss),
            Pair(Pair(0, 0), Map.HitState.Empty),
            Pair(Pair(6, 9), Map.HitState.Hit),
            Pair(Pair(7, 9), Map.HitState.Hit),
            Pair(Pair(8, 9), Map.HitState.Hit),
            Pair(Pair(9, 9), Map.HitState.Hit),
            Pair(Pair(9, 0), Map.HitState.Miss),
            Pair(Pair(9, 9), Map.HitState.Empty),
        )
        val map = Map(false)
        map.setBoat(6, 9, 4, true)
        testData.forEach {
            assertEquals(it.second, map.hit(it.first.first, it.first.second), it.toString())
            assertTrue(map.cells[it.first.first][it.first.second].isHitCell, it.first.toString())
        }
        (5..9).forEach {
            assertTrue(map.cells[it][8].isHitCell, "$it 8")
        }
        assertTrue(map.cells[5][9].isHitCell, "5 8")
    }

    @Test
    fun testSetHit() {
        val testData = arrayOf(
            Pair(0, 0),
            Pair(6, 9),
            Pair(9, 0),
            Pair(9, 9),
            Pair(-1, -1),
            Pair(10, 5),
        )
        val map = Map(false)
        testData.forEach {
            Whitebox.invokeMethod<Unit>(map, "setHit", it.first, it.second)
            if (it.first in 0..9 && it.second in 0..9)
                assertTrue(map.cells[it.first][it.second].isHitCell, it.toString())
        }
    }

    @Test
    fun testIsDead() {
        val map = Map(false)
        assertTrue(map.isFleetDead())
        map.setBoat(6, 9, 4, true)
        assertFalse(map.isFleetDead())
        map.hit(6, 9)
        assertFalse(map.isFleetDead())
        map.hit(9, 9)
        assertFalse(map.isFleetDead())
        map.hit(7, 9)
        map.hit(8, 9)
        assertTrue(map.isFleetDead())
    }
}
