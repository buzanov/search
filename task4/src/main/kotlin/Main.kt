import java.io.File
import java.io.FileWriter
import java.text.DecimalFormat
import java.util.*
import kotlin.math.log10

const val documentsPath = "../task1/src/main/resources/выкачка/"
const val documentsCount = 99
private val service = LemmatizationService()
private const val tokensDocumentPath = "src/main/resources/tokens/"
private const val lemmaDocumentPath = "src/main/resources/lemmas/"

val lemmaMap = mutableMapOf<Int, MutableMap<String, Int>>()
val tokenMap = mutableMapOf<Int, MutableMap<String, Int>>()

fun main(args: Array<String>) {
    var scanner: Scanner
    for (i in 0..documentsCount) {
        scanner = Scanner(File("${documentsPath}выкачка$i.txt"))

        scanner.iterator().forEach { scannedString ->
            //Это регулярное выражение нужно чтобы убрать все небуквенные символы
            scannedString
                .replace("ё", "е")
                .split("[^а-яА-Яa-zA-Z]".toRegex())
                .filter { it.isNotBlank() }
                .forEach {
                    //Это регулярное выражение разделяет строки типа "ЗвездыПсихологияЕда.." в отдельные токены
                    it.split("(?<=[а-я])(?=[А-Я])".toRegex()).forEach token@{ token ->
                        tokenMap.compute(i) { _, v ->
                            if (v == null) {
                                val map = mutableMapOf<String, Int>()
                                map[token] = 1
                                map
                            } else {
                                v.compute(token) { _, k -> (k ?: 1).plus(1) }
                                v
                            }
                        }
                        val lemma = service.getLemmaOfString(token) ?: return@token
                        lemmaMap.compute(i) { _, v ->
                            if (v == null) {
                                val map = mutableMapOf<String, Int>()
                                map[lemma] = 1
                                map
                            } else {
                                v.compute(lemma) { _, k -> (k ?: 1).plus(1) }
                                v
                            }
                        }
                    }
                }
        }
        println("Processed $i document")
    }

    val decimalFormat = DecimalFormat("0.000000")
    for (i in 0..documentsCount) {
        val countWordsInDocument = lemmaMap[i]!!.entries.map { it.value }.reduce { a, c -> a + c }
        val fileWriter = FileWriter(File("${lemmaDocumentPath}/document$i.txt"))
        lemmaMap[i]!!.forEach { e ->
            val lemma = e.key
            val tf = e.value.toDouble() / countWordsInDocument

            val count = lemmaMap.map { it.value }.count { it[lemma] != null }.toDouble()
            val idf = log10((100 / (count + 1)))
            val tfIdf = tf * idf

            fileWriter.write("$lemma ${decimalFormat.format(idf)} ${decimalFormat.format(tfIdf)}\n")
        }
        fileWriter.flush()
        println("lemma idf-index for document $i processed.")
    }
    for (i in 0..documentsCount) {
        val countWordsInDocument = tokenMap[i]!!.entries.map { it.value }.reduce { a, c -> a + c }
        val fileWriter = FileWriter(File("${tokensDocumentPath}/document$i.txt"))
        tokenMap[i]!!.forEach { e ->
            val lemma = e.key
            val tf = e.value.toDouble() / countWordsInDocument

            val count = tokenMap.map { it.value }.count { it[lemma] != null }.toDouble()
            val idf = log10((100 / (count + 1)))
            val tfIdf = tf * idf

            fileWriter.write("$lemma ${decimalFormat.format(idf)} ${decimalFormat.format(tfIdf)}\n")
        }
        fileWriter.flush()
        println("token idf-index for document $i processed.")
    }
}