/* The output consists of any two factors out of all the possible factors of N.
 * These might differ with every run based on our choice of coprime integer x, and
 * because Shor's algorithm has a probabilistic model in this classical implementation.
 *
 * Conditions for the algorithm to work:
 *      - N should not be a prime number
 *      - N should not be an even number
 *      - N and x should be coprime integers (i.e. only common factor should be 1)
 *      - N^2 <= 2^t <= 2N^2
 */

fun main() {
    println("=============================================")
    println("Example 1: Factoring 21")
    val shorAlgorithm1 = ShorAlgorithm(x = 2, N = 21, t = 9)
    shorAlgorithm1.runShorAlgorithm()

    println("=============================================")
    println("Example 2: Factoring 15")
    val shorAlgorithm2 = ShorAlgorithm(x = 2, N = 15, t = 8)
    shorAlgorithm2.runShorAlgorithm()

    println("=============================================")
    println("Example 3: Factoring 33")
    val shorAlgorithm3 = ShorAlgorithm(x = 2, N = 33, t = 12)
    shorAlgorithm3.runShorAlgorithm()

    println("=============================================")
}