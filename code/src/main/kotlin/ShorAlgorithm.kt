import java.util.*
import kotlin.math.*

/**
 * A classical implementation of Shor's Algorithm for factoring integers into their prime factors.
 *
 * The output consists of any two factors out of all the possible factors of N.
 * These might differ with every run based on our choice of coprime integer x, and
 * because Shor's algorithm has a probabilistic model in this classical implementation.
 *
 * Conditions for the algorithm to work:
 *      - N should not be a prime number
 *      - N should not be an even number
 *      - N and x should be coprime integers (i.e. only common factor should be 1)
 *      - N^2 <= 2^t <= 2N^2
 *
 * @property x The integer to find the coprime of N.
 * @property N The integer to be factored.
 * @property T The size of the quantum registers, calculated as 2^t.
 */
@Suppress("PrivatePropertyName")
class ShorAlgorithm(private val x: Int, private val N: Int, t: Int) {

    private val T = 2.0.pow(t).toInt()  // T = 2^t such that N^2 <= T <= 2N^2

    private val argumentRegister = Array(T) { 0 }
    private val functionRegister = Array(T) { 0 }

    init {
        initializeArgumentRegister()
        performModularExponentiation()
    }

    /**
     * Initializes the quantum argument register.
     */
    private fun initializeArgumentRegister() {
        for (a in 0 until T) {
            argumentRegister[a] = a
        }
    }

    /**
     * Performs modular exponentiation on the function register.
     */
    private fun performModularExponentiation() {
        for (a in 0 until T) {
            functionRegister[a] = (x.toDouble().pow(a) % N).toInt()
        }
    }

    /**
     * Runs Shor's Algorithm to factor the given integer N.
     */
    fun runShorAlgorithm() {
        val measuredFunctionValue = measureFunctionRegister()
        val (periodNum, periodNumOffset) = preprocessDataForQFT(measuredFunctionValue)
        val (extractedValues, a) = calculateQFTInputValues(periodNum, periodNumOffset)
        val period = calculatePeriod(extractedValues, a, periodNum)
        val factors = findFactors(period)
        println("Some factors of $N are: $factors")
    }

    /**
     * Measures a random value from the function register.
     *
     * @return The measured value.
     */
    private fun measureFunctionRegister(): Int {
        return functionRegister.random()
    }

    /**
     * Preprocesses data for the Quantum Fourier Transform (QFT).
     *
     * @param measuredFunctionValue The measured value from the function register.
     * @return A pair containing the number of distinct values and the offset of the measured value.
     */
    private fun preprocessDataForQFT(measuredFunctionValue: Int): Pair<Int, Int> {
        val possibleValues = functionRegister.distinct()
        val patternNum = possibleValues.size
        val patternNumOffset = possibleValues.indexOf(measuredFunctionValue)
        return patternNum to patternNumOffset
    }

    /**
     * Calculates input values for the Quantum Fourier Transform (QFT).
     *
     * @param patternNum The number of distinct values.
     * @param patternNumOffset The offset of the measured value.
     * @return A pair containing the extracted values and the parameter 'a'.
     */
    private fun calculateQFTInputValues(patternNum: Int, patternNumOffset: Int): Pair<Array<Int>, Int> {
        val patternExtractedValues = Array(T) { 0 }
        for (i in 0 until T) {
            val index = patternNum * i + patternNumOffset
            if (index < T) {
                patternExtractedValues[i] = argumentRegister[index]
            }
        }
        val droppedValue = patternExtractedValues[0]
        val tempExtractedValues = patternExtractedValues.drop(1).takeWhile { it != 0 }.toMutableList()
        tempExtractedValues.add(0, droppedValue)
        val extractedValues = tempExtractedValues.toTypedArray()
        val a = ((extractedValues.last() - patternNumOffset) / patternNum) + 1
        return extractedValues to a
    }

    /**
     * Calculates the period using Quantum Fourier Transform (QFT) output.
     *
     * @param extractedValues The extracted values from the argument register.
     * @param a The calculated value of 'a' used for QFT.
     * @param patternNum The number of distinct values.
     * @return The calculated period.
     */
    private fun calculatePeriod(extractedValues: Array<Int>, a: Int, patternNum: Int): Int {
        val qftOutput = quantumFourierTransform(extractedValues)
        val measuredArgumentValue = measureArgumentRegister(qftOutput, a, patternNum)
        return T / measuredArgumentValue
    }

    /**
     * Applies Quantum Fourier Transform (QFT) on the input array.
     *
     * @param input The input array.
     * @return The output array after QFT.
     */
    private fun quantumFourierTransform(input: Array<Int>): Array<Double> {
        val output = Array(T) { 0.0 }
        for (j in 0 until T) {
            var sumReal = 0.0
            var sumImaginary = 0.0
            for (k in input.indices) {
                val angle = 2 * PI * 2 * j * k / T.toDouble()
                val cosTheta = cos(angle)
                val sinTheta = sin(angle)
                sumReal += input[k] * cosTheta
                sumImaginary += input[k] * sinTheta
            }
            output[j] = sumReal / T + sumImaginary / T
        }
        return output
    }

    /**
     * Measures the argument register after QFT to determine the period.
     *
     * @param input The QFT output array.
     * @param a The calculated value of 'a' used for QFT.
     * @param patternNum The number of distinct values.
     * @return The measured value representing the period.
     */
    private fun measureArgumentRegister(input: Array<Double>, a: Int, patternNum: Int): Int {
        val probabilities = Array(a) { 0.0 }

        for (j in 0 until a) {
            probabilities[j] = (input[j].pow(2) / T) / a
        }

        val maxProbabilities = PriorityQueue<Pair<Int, Double>>(compareByDescending { it.second })
        for (j in probabilities.indices) {
            maxProbabilities.offer(j to probabilities[j])
            if (maxProbabilities.size > patternNum) {
                maxProbabilities.poll()
            }
        }

        val maxIndices = maxProbabilities.map { it.first }.toSet()
        return maxIndices.random()
    }

    /**
     * Finds the factors of N using the calculated period.
     *
     * @param period The calculated period.
     * @return The list of factors.
     */
    private fun findFactors(period: Int): List<Int> {
        val factors = mutableListOf<Int>()

        if (period % 2 != 0) {
            factors.add(1)
            factors.add(N)
            return factors
        }

        factors.add(gcd(x.toDouble().pow(period / 2).toInt() + 1, N))
        factors.add(gcd(x.toDouble().pow(period / 2).toInt() - 1, N))

        return factors.map { it.absoluteValue }
    }

    /**
     * Calculates the greatest common divisor (GCD) of two integers using Euclid's algorithm.
     *
     * @param a The first integer.
     * @param b The second integer.
     * @return The GCD of 'a' and 'b'.
     */
    private fun gcd(a: Int, b: Int): Int {
        return if (b == 0) a else gcd(b, a % b)
    }
}