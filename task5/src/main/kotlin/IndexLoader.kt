import java.io.File
import java.util.*

const val documentsPath = "../task1/src/main/resources/выкачка/"
private const val documentPath = "../task4/src/main/resources/lemmas/"
private const val indexPath = "../task1/src/main/resources/index.txt"
private const val documentsCount = 99

class IndexLoader {
    val matrix = mutableMapOf<String, Array<Double>>()
    lateinit var stringArray: Array<String>
    lateinit var arrayMatrix: Array<Array<Double>>
    val index = Array(100) { "" }
    val lemmaMap = mutableMapOf<Int, MutableMap<String, Int>>()
    private val service = LemmatizationService()

    init {
        loadMatrix()
        loadIndex()
        loadLemmaMap()
    }

    private fun loadIndex() {
        val scanner = Scanner(File(indexPath))
        var i = 0
        while (scanner.hasNext()) {
            val read = scanner.nextLine().split(" ")
            index[read[2].replace("выкачка|.txt".toRegex(), "").toInt()] = read[0]
            i++
        }
    }

    private fun loadMatrix() {
        for (i in 0..documentsCount) {
            val scanner = Scanner(File("${documentPath}document$i.txt"))

            while (scanner.hasNext()) {
                val input = scanner.nextLine().split(" ")

                val key = input[0]
                val tfIdf = input[2].replace(",", ".").toDouble()

                matrix.compute(key) { _, v ->
                    if (v == null) {
                        val arr = Array(100) { 0.toDouble() }
                        arr[i] = tfIdf
                        arr
                    } else {
                        v[i] = tfIdf
                        v
                    }
                }
            }
        }
        stringArray = Array(matrix.size) { "" }
        arrayMatrix = Array(matrix.size) { Array(100) { 0.toDouble() } }
        matrix.entries.forEachIndexed { index, entry ->
            stringArray[index] = entry.key
            arrayMatrix[index] = entry.value
        }
    }

    private fun loadLemmaMap() {
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
        }

    }
}