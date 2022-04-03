import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.morphology.russian.RussianAnalyzer
import org.apache.lucene.morphology.russian.RussianMorphology
import java.io.File
import java.io.FileWriter
import java.io.StringReader
import java.util.*

const val directory = "../task1/src/main/resources/выкачка/"
const val firstChar = 'а'
const val lastChar = 'я'
val stopTypes = "ПРЕДЛ|МЕЖД|СОЮЗ|ЧАСТ^+".toRegex()
val analyzer = RussianMorphology()


fun validate(str: String): String {
    val s = str
        .lowercase()
        .filter { it1 -> it1 in firstChar..lastChar }
        .replace("ё", "е")
    if (s.isNotBlank()) {
        val info = analyzer.getMorphInfo(s)
        val isPermitted = !info.any { it.contains(stopTypes) }
        return if (isPermitted) {
            s
        } else {
            ""
        }
    }
    return s
}

fun main(args: Array<String>) {
    val map = mutableMapOf<String, MutableSet<String>>()

    val analyzer = RussianAnalyzer()

    for (i in 0..99) {
        var reader: StringReader
        val scanner = Scanner(File(directory + "выкачка$i.txt"))
        while (scanner.hasNext()) {
            var str: String
            var lemma: String
            scanner.forEach {
                str = validate(it)
                if (str.isNotBlank()) {
                    reader = StringReader(str)
                    val stream = analyzer.tokenStream("field", reader)
                    stream.reset()
                    if (stream.incrementToken()) {
                        lemma = stream.getAttribute(CharTermAttribute::class.java).toString()
                        map.compute(lemma) { _, v ->
                            if (v == null) {
                                mutableSetOf(str)
                            } else {
                                v.add(str)
                                v
                            }
                        }
                    }
                    stream.end()
                    stream.close()

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

    val lemmasFileWriter = FileWriter(lemmasFile)
    val tokensFileWriter = FileWriter(tokensFile)

    map.values.forEach {
        it.forEach { it1 ->
            tokensFileWriter.write("$it1 \n")
        }
    }
    tokensFileWriter.flush()
    map.entries.forEach {
        lemmasFileWriter.write(it.key + ": " + it.value.joinToString(separator = " ") + "\n")
    }

    lemmasFileWriter.flush()
}