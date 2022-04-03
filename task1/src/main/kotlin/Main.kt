import org.jsoup.Jsoup
import java.io.File
import java.io.FileWriter
import java.util.*

fun main(args: Array<String>) {
    val file = File("src/main/resources/list.txt")
    val scanner = Scanner(file)

    val list = mutableListOf<String>()

    var str: String
    while (scanner.hasNext()) {
        str = scanner.nextLine()
        list.add(str)
    }

    val directory = File("src/main/resources/выкачка")
    if (!directory.exists()) {
        directory.mkdir()
    }

    val indexFileWriter = FileWriter(File("src/main/resources/index.txt"))

    val errorList = mutableListOf<String>()

    list.forEachIndexed { index, string ->
        try {
            val response = Jsoup.connect("https://$string").get().text()
            val fileName = "выкачка$index.txt"
            val result = File("src/main/resources/выкачка/$fileName")
            result.createNewFile()

            FileWriter(result).use { it.write(response) }

            indexFileWriter.write("$string -> $fileName \n")
            println("${index + 1}%: $string processed")
        } catch (e: Exception) {
            errorList.add(string)
            println("$string error during processing " + e.message)
        }
    }
    indexFileWriter.flush()

}