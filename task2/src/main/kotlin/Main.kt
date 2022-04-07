import java.io.File
import java.io.FileWriter
import java.nio.charset.Charset
import java.util.*

const val directory = "../task1/src/main/resources/выкачка/"

fun main(args: Array<String>) {
    val service = LemmatizationService()
    val map = mutableMapOf<String, MutableSet<String>>()

    for (i in 0..99) {
        val scanner = Scanner(File(directory + "выкачка$i.txt"))
        scanner.iterator().forEach { scannedString ->
            //Это регулярное выражение нужно чтобы убрать все небуквенные символы
            scannedString.split("[^а-яА-ЯёЁ]".toRegex())
                .filter { it.isNotBlank() }
                .forEach {
                    //Это регулярное выражение разделяет строки типа "ЗвездыПсихологияЕда.." в отдельные токены
                    it.split("(?<=[а-я])(?=[А-Я])".toRegex()).forEach token@{ token ->
                        val lemma = service.getLemmaOfString(token) ?: return@token

                        map.compute(lemma) { _, v ->
                            if (v == null) {
                                mutableSetOf(token)
                            } else {
                                v.add(token)
                                v
                            }
                        }
                    }
                }
        }
        println("$i% processed.")
    }

    val lemmasFile = File("src/main/resources/lemmas.txt")
    if (!lemmasFile.exists())
        lemmasFile.createNewFile()

    val tokensFile = File("src/main/resources/tokens.txt")
    if (!tokensFile.exists())
        tokensFile.createNewFile()

    val lemmasFileWriter = FileWriter(lemmasFile, Charset.forName("windows-1251"), false)
    val tokensFileWriter = FileWriter(tokensFile, Charset.forName("windows-1251"), false)

    map.values.forEach {
        it.forEach { it1 ->
            tokensFileWriter.append("$it1 \n")
        }
    }
    tokensFileWriter.flush()
    map.entries.forEach {
        lemmasFileWriter.append(it.key + ": " + it.value.joinToString(separator = " ") + "\n")
    }

    lemmasFileWriter.flush()
}