import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import model.Page
import java.text.DecimalFormat

const val SEARCH_PLACEHOLDER = "Введите текст для поиска"

@Composable
@Preview
fun app() {
    var input by remember { mutableStateOf("") }
    var searchTypeIsMatchingScore by remember { mutableStateOf(true) }
    var resultFound by remember { mutableStateOf(false) }
    var searchResult by remember { mutableStateOf(List(5) { Page() }) }
    val searcher = IndexSearcher()


    fun search(input: String) {
        searchResult = if (searchTypeIsMatchingScore) {
            searcher.matchingScoreSearch(input)
        } else {
            searcher.tfIdfSearch(input)
        }
        resultFound = true
    }

    MaterialTheme {
        Column {
            Row {
                Switch(
                    checked = searchTypeIsMatchingScore,
                    onCheckedChange = {
                        searchTypeIsMatchingScore = !searchTypeIsMatchingScore
                    }
                )
                Text(
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = if (searchTypeIsMatchingScore) "MatchingScore" else "Tf-Idf cosine similarity"
                )
            }
            Row(
                Modifier.fillMaxWidth().padding(top = 25.dp, start = 35.dp, end = 25.dp),
                Arrangement.Start,
                Alignment.Top
            ) {
                TextField(
                    textStyle = TextStyle(fontSize = 13.sp),
                    value = input,
                    modifier = Modifier.size(550.dp, 60.dp).align(Alignment.CenterVertically).padding(5.dp),
                    singleLine = true,
                    label = { Text(SEARCH_PLACEHOLDER, color = Color.Gray) },
                    onValueChange = {
                        input = it
                    })
                Button(
                    modifier = Modifier.size(90.dp, 50.dp).padding(end = 5.dp).align(Alignment.CenterVertically),
                    content = { Text("Search") },
                    onClick = {
                        search(input)
                    }
                )
                Button(
                    modifier = Modifier.size(90.dp, 50.dp).padding(start = 5.dp).align(Alignment.CenterVertically),
                    content = { Text("Clear") },
                    onClick = {
                        resultFound = false
                        searchResult = emptyList()
                        input = ""
                    }
                )
            }
        }
        Column(Modifier.fillMaxWidth().padding(top = 130.dp, start = 50.dp, end = 25.dp)) {
            if (resultFound)
                searchResult(searchResult)
        }

    }
}

fun main() = application {
    Window(
        title = "Buzanov-Search",
        state = WindowState(width = 800.dp, height = 600.dp),
        resizable = false,
        onCloseRequest = ::exitApplication
    ) {
        app()
    }
}

@Composable
fun searchResult(result: List<Page>) {
    result.forEach {
        Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
            Column {
                Text(it.url!!, fontWeight = FontWeight.Bold)
                Text(it.words!!)
                Text(DecimalFormat("0.0000000").format(it.q!!))
            }
        }
    }
}
