import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.morphology.russian.RussianAnalyzer
import org.apache.lucene.morphology.russian.RussianMorphology
import java.io.StringReader

const val firstChar = 'а'
const val lastChar = 'я'

class LemmatizationService {
    private val stopTypes = "ПРЕДЛ|МЕЖД|СОЮЗ|ЧАСТ^+".toRegex()
    private val morphology = RussianMorphology()
    private val russianAnalyzer = RussianAnalyzer()


    private fun validate(str: String): String {
        val s = str
            .lowercase()
            .filter { it1 -> it1 in firstChar..lastChar }
            .replace("ё", "е")

        if (s.isNotBlank()) {
            val info = morphology.getMorphInfo(s)
            if (!info.any { !it.contains(stopTypes) })
                return ""
        }
        return s
    }

    fun getLemmaOfString(input: String): String? {

        val str = validate(input)
        if (str.isNotBlank()) {
            val reader = StringReader(str)
            val stream = russianAnalyzer.tokenStream("field", reader)
            stream.reset()
            if (stream.incrementToken()) {
                return stream.getAttribute(CharTermAttribute::class.java).toString()
            }
            stream.end()
            stream.close()
        }
        return null
    }
}