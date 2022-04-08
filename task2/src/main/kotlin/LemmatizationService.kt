import org.apache.lucene.analysis.TokenStream
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.morphology.EnglishAnalyzer
import org.apache.lucene.morphology.EnglishMorphology
import org.apache.lucene.morphology.russian.RussianAnalyzer
import org.apache.lucene.morphology.russian.RussianMorphology
import java.io.StringReader

class LemmatizationService {
    private val rusStopTypes = "ПРЕДЛ|МЕЖД|СОЮЗ|ЧАСТ^+".toRegex()
    private val engStopTypes = "CONJ|PART|ARTICLE|PREP^+".toRegex()
    private val morphology = RussianMorphology()
    private val russianAnalyzer = RussianAnalyzer()
    private val engMorphology = EnglishMorphology()
    private val engAnalyzer = EnglishAnalyzer()


    fun getLemmaOfString(input: String): String? {
        val eng = input.contains("[a-zA-Z]".toRegex())
        val rus = input.contains("[а-яА-Я]".toRegex())

        if (input.isBlank()) return null
        if (eng && rus) return null
        val stream: TokenStream
        val string = input.lowercase()
        val reader = StringReader(string)
        if (eng) {
            val info = engMorphology.getMorphInfo(string)
            if (!info.any { !it.contains(engStopTypes) }) return null
            stream = russianAnalyzer.tokenStream("field", reader)
            stream.reset()
            if (stream.incrementToken()) {
                return stream.getAttribute(CharTermAttribute::class.java).toString()
            }
        } else {
            val info = morphology.getMorphInfo(string)
            if (!info.any { !it.contains(rusStopTypes) }) return null
            stream = engAnalyzer.tokenStream("field", reader)
            stream.reset()
            if (stream.incrementToken()) {
                return stream.getAttribute(CharTermAttribute::class.java).toString()
            }
        }
        stream.end()
        stream.close()

        return null
    }
}