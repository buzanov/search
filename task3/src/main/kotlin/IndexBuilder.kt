import java.io.File
import java.io.FileWriter
import java.util.Scanner

const val documentsPath = "../task1/src/main/resources/выкачка/"
const val documentsCount = 99
private val service = LemmatizationService()
private val indexFile = File("src/main/resources/index.txt")
private val fileWriter = FileWriter(indexFile, false)

fun main(args: Array<String>) {
    val map = mutableMapOf<String, MutableSet<Int>>()

    var scanner: Scanner
    for (i in 0..documentsCount) {
        scanner = Scanner(File("${documentsPath}выкачка$i.txt"))

        scanner.iterator().forEach { scannedString ->
            //Это регулярное выражение нужно чтобы убрать все небуквенные символы
            scannedString
                .replace("ё","е")
                .split("[^а-яА-Яa-zA-Z]".toRegex())
                .filter { it.isNotBlank() }
                .forEach {
                    //Это регулярное выражение разделяет строки типа "ЗвездыПсихологияЕда.." в отдельные токены
                    it.split("(?<=[а-я])(?=[А-Я])".toRegex()).forEach token@{ token ->
                        val lemma = service.getLemmaOfString(token) ?: return@token

                        map.compute(lemma) { _, v ->
                            if (v == null) {
                                mutableSetOf(i)
                            } else {
                                v.add(i)
                                v
                            }
                        }
                    }
                }
        }
        println("Processed $i document")
    }

    map.forEach { (key, value) ->
        fileWriter.write("word:$key;count:${value.size};documents:$value\n")
    }

    fileWriter.flush()
}