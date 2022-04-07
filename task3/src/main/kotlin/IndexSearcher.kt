import java.io.File
import java.util.*


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
    val result = mutableSetOf<Int>()
    val lemmas = query
        .replace("ё", "е")
        .split(" ")
        .mapNotNull { service.getLemmaOfString(it) }

    if (lemmas.isNotEmpty()) {
        val r = lemmas.stream()
            .map { it.lowercase() }
            .map { indexMap[it] }
            .filter { it != null }
            .reduce { set1, set2 -> set1!!.intersect(set2!!) }

        if (r.isPresent)
            result.addAll(r.get())
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