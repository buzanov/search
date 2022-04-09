import model.Page
import kotlin.math.log10

class IndexSearcher {
    private val loader = IndexLoader()
    private val stringArray = loader.stringArray
    private val arrayMatrix = loader.arrayMatrix
    private val matrix = loader.matrix
    private val index = loader.index
    private val lemmaMap = loader.lemmaMap
    private val service = LemmatizationService()

    fun matchingScoreSearch(query: String): List<Page> {
        val lemmas = mutableSetOf<String>()
        query.split(" ").forEach {
            val token = it.replace("[^a-zA-Zа-яА-Я]".toRegex(), "").lowercase().replace("ё", "е")
            val lemma = service.getLemmaOfString(token) ?: return@forEach

            lemmas.add(lemma)
        }
        val score = matrix
            .entries
            .stream()
            .filter { lemmas.contains(it.key) }
            .map { it.value }
            .reduce { a, c -> a.mapIndexed { index, d -> d + c[index] }.toTypedArray() }


        if (!score.isPresent) return listOf()
        val result = score.get()
            .mapIndexed { index, d -> Pair(index, d) }
            .sortedByDescending { it.second }
            .subList(0, 5)

        return result
            .filter { it.second != 0.toDouble() }
            .map { pair ->
                Page(
                    words = matrix
                        .map { it.key to it.value[pair.first] }
                        .sortedByDescending { it.second }
                        .subList(0, 5).joinToString(separator = " ") { it.first },
                    url = index[pair.first],
                    q = pair.second
                )
            }.toList()
    }

    fun tfIdfSearch(query: String): List<Page> {
        val lemmas = mutableMapOf<String, Int>()
        query.split(" ").forEach {
            val token = it.replace("[^a-zA-Zа-яА-Я]".toRegex(), "").lowercase().replace("ё", "е")
            val lemma = service.getLemmaOfString(token) ?: return@forEach

            lemmas.merge(lemma, 1) { v1, v2 -> v1 + v2 }
        }
        val vector = Array(matrix.size) { 0.toDouble() }
        lemmas.forEach { e ->
            val countWordsInDocument = lemmas.map { it.value }.reduce { v1, v2 -> v1 + v2 }
            val lemma = e.key
            val tf = e.value.toDouble() / countWordsInDocument

            val count = lemmaMap.map { it.value }.count { it[lemma] != null }.toDouble()
            val idf = log10((100 / (count + 1)))
            val tfIdf = tf * idf

            val index = stringArray.indexOf(e.key)

            vector[index] = tfIdf
        }
        val score = mutableListOf<Pair<Int, Double>>()
        for (i in 0..99) {
            val documentVector = arrayMatrix.map { it[i] }.toTypedArray()
            score.add(Pair(i, Utils.cosineSimilarity(documentVector.toDoubleArray(), vector.toDoubleArray())))
        }
        return score
            .sortedByDescending { it.second }
            .subList(0, 5)
            .filter { it.second != 0.toDouble() }
            .map { pair ->
                Page(
                    words = matrix
                        .map { it.key to it.value[pair.first] }
                        .sortedByDescending { it.second }
                        .subList(0, 5).joinToString(separator = " ") { it.first },
                    url = index[pair.first],
                    q = pair.second
                )
            }
    }
}