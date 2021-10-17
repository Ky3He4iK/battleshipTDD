import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import controller.Game
import view.app

fun main() = singleWindowApplication(resizable= false,
    title = "Кораблики", state = WindowState(size = WindowSize(660.dp, 500.dp))
) {
    val game = Game()
    app(game)
}
