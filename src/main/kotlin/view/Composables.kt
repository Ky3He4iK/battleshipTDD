package view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import controller.Game
import entity.Point
import kotlinx.coroutines.launch

@Composable
fun app(game: Game) = MaterialTheme() {
        val state = rememberSaveable  { game.state }
        val isVertical = rememberSaveable  { mutableStateOf(game.isVertical) }
        when (state.value) {
            Game.GameState.PlayersWin -> {
                Column(modifier = Modifier.width(660.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(modifier = Modifier.height(200.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            Game.GameState.PlayersWin.textState,
                            color = Color.Blue,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    restartButton(game)
                }
            }
            Game.GameState.BotsWin -> {
                Column(modifier = Modifier.width(660.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(modifier = Modifier.height(200.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            Game.GameState.BotsWin.textState,
                            color = Color.Red,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    restartButton(game)
                }
            }
            else -> content(game, isVertical)
        }
    }


@Composable
fun content(
    game: Game,
    isHorizontal: MutableState<Boolean>
) = Column(modifier = Modifier.padding(10.dp)) {
    Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
        TextButton(
            onClick = { game.restart() },
            modifier = Modifier.background(Color(148, 219, 233))
        ) {
            Text("Начать заново")
        }
    }
    Box(Modifier.height(20.dp))
    val state = rememberSaveable { mutableStateOf(game.state) }
    if (state.value.value == Game.GameState.Prepare)
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(text = "Разместить корабль вертикально?")
            Checkbox(checked = isHorizontal.value,
                onCheckedChange = {
                    isHorizontal.value = !isHorizontal.value
                    game.isVertical = isHorizontal.value
                })
        }
    Row {
        userField(game, "Пользователь", isHorizontal, false)
        Box(modifier = Modifier.width(10.dp).fillMaxHeight())
        userField(game, "Бот", isHorizontal, true)
    }
}
@Composable
fun userField(game: Game, name: String, isHorizontal: MutableState<Boolean>, isBotField: Boolean) =
    Column {
        Text(name)
        field(game, isHorizontal, isBotField)
    }

@Composable
fun field(game: Game, isHorizontal: MutableState<Boolean>, isBotField: Boolean) {
    val boxSize = 30.dp

    val renew = rememberSaveable { mutableStateOf(true) }
    if (renew.value)
        Row {
            for (j in 0 until 10) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End,
                ) {
                    for (i in 0 until 10) {
                        var point = if (isBotField) game.botsMap.cells[i][j] else game.playersMap.cells[i][j]
                        val backcolor = rememberSaveable { mutableStateOf(getColor(point, isBotField)) }
                        Card(modifier = Modifier.size(boxSize, boxSize)
                            .padding(1.dp)
                            .clickable(onClick = {
                                game.click(i, j, isHorizontal.value, isBotField)
                                point = if (isBotField) game.botsMap.cells[i][j] else game.playersMap.cells[i][j]
                                backcolor.value = getColor(point, isBotField)
                            }),
                            backgroundColor = backcolor.value,
                            content = {}
                        )
                        val coroutineScope = rememberCoroutineScope()
                        coroutineScope.launch {
                            point = if (isBotField)
                                game.botsMap.cells[i][j]
                            else
                                game.playersMap.cells[i][j]
                            backcolor.value = getColor(point, isBotField)
                        }
                    }
                }
            }
        }
}

fun getColor(point: Point, isBotField: Boolean) : Color {
    return if (point.isBoatCell)
        if (isBotField)
            if (point.isHitCell) Color.Red
            else Color(110, 146, 255)
        else
            if (point.isHitCell) Color.Red
            else Color.DarkGray
    else
        if (point.isHitCell) Color.Blue
        else Color(110, 146, 255)
}


@Composable
fun restartButton(game: Game) =
    Row(modifier = Modifier.height(200.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.height(30.dp))
        TextButton(
            onClick = { game.restart() },
            modifier = Modifier.background(Color(148, 219, 233))
        ) {
            Text("Начать заново")
        }
    }