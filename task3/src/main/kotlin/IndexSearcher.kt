import java.io.File
import java.util.*

class Lemma(
    val lemma: String,
    var type: Type
)

enum class Type {
    AND, OR, NOT
}

private val indexFile = File("src/main/resources/index.txt")
private val indexMap = mutableMapOf<String, Set<Int>>()
private val service = LemmatizationService()

fun main(args: Array<String>) {
    loadIndex()

    val scanner = Scanner(System.`in`)

    var query = scanner.nextLine()
    while (query != "exit") {
        println(booleanSearch(query))

        query = scanner.nextLine()
    }
}


fun booleanSearch(query: String): Set<Int> {
    var result = mutableSetOf<Int>()
    val lemmas = mutableListOf<Lemma>()
    query.replace("ё", "е")
        .split(" ")
        .forEach {
            when (it) {
                Type.AND.name -> lemmas.last().type = Type.AND
                Type.NOT.name -> lemmas.last().type = Type.NOT
                Type.OR.name -> lemmas.last().type = Type.OR
                else -> service.getLemmaOfString(it)?.let { it1 ->
                    lemmas.add(Lemma(it1, Type.AND))
                }
            }
        }

    if (lemmas.isNotEmpty()) {
        var i = 0
        var lastOperator = Type.AND
        lemmas.iterator().forEachRemaining {
            if (i == 0)
                result.addAll(indexMap[it.lemma.lowercase()]!!)
            else {
                when (lastOperator) {
                    Type.AND -> result = result.intersect(indexMap[it.lemma.lowercase()]!!).toMutableSet()
                    Type.OR -> result = result.union(indexMap[it.lemma.lowercase()]!!).toMutableSet()
                    Type.NOT -> result = result.subtract(indexMap[it.lemma.lowercase()]!!).toMutableSet()
                }
            }
            lastOperator = it.type
            i += 1
        }
    }
    return result
}


fun loadIndex() {
    val scanner = Scanner(indexFile)
    while (scanner.hasNext()) {
        val value = scanner.nextLine().split(";")

        val word = value[0].split(":")[1]
        val documents = value[2]
            .split(":")[1]
            .replace("[", "")
            .replace("]", "")
            .split(", ")
            .map { it.toInt() }
            .toSet()

        indexMap[word] = documents
    }
    println("Index loaded")
}