import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class Game {
    var state: MutableState<GameState>
    enum class GameState(val textState: String) {
        WaitingState("Ожидайте"),
        Prepare("Расстановка кораблей."),
        PlayersTurn("Ваш ход"),
        BotsTurn("Ход бота"),
        PlayersWin("Вы победили!!!"),
        BotsWin("Вы проиграли :("),
        Error("Критическая ошибка")
    }

    init {
        state = mutableStateOf(GameState.Prepare)
    }

}
