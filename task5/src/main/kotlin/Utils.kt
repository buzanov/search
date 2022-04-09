import kotlin.math.sqrt

class Utils {
    companion object {
        fun cosineSimilarity(vec1: DoubleArray, vec2: DoubleArray): Double {
            return vectorDot(vec1, vec2) / (vectorNorm(vec1) * vectorNorm(vec2))
        }

        private fun vectorDot(vec1: DoubleArray, vec2: DoubleArray): Double {
            var sum = 0.0
            var i = 0
            while (i < vec1.size && i < vec2.size) {
                sum += vec1[i] * vec2[i]
                i++
            }
            return sum
        }

        private fun vectorNorm(vec: DoubleArray): Double {
            var sum = 0.0
            for (v in vec) sum += v * v
            return sqrt(sum)
        }
    }

}