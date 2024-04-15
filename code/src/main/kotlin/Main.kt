import kotlin.math.*

fun main() {
    val x = 2   // coprime integer 1
    val N = 21  // coprime integer 2

    val t = 9   // number of qubits for argument register
    val n = log2(N.toDouble()).toInt() // Number of qubits for function register

    // T = 2^t such that N^2 <= T <= 2N^2
    val T = 2.0.pow(t).toInt()

    // Initialize registers
    val argumentRegister = Array(T) { 0.0 }
    val functionRegister = Array(T) { 0.0 }

    // apply a Hadamard gate on each of the qubits in the argument register
    for (a in 0 until T) {
        argumentRegister[a] = a.toDouble()
    }

    // implement the modular exponentiation function on the function register
    for (a in 0 until T) {
        functionRegister[a] = x.toDouble().pow(a) % N
    }

    // Measure the function register

    // Perform a quantum Fourier transform on the argument register

    // Measure the argument register

    // Find the period for obtaining the result
}


fun quantumFourierTransform(input: Array<Double>) {

}

fun measureFunctionRegister(input: Array<Double>) {

}

fun measureArgumentRegister(input: Array<Double>) {

}

fun findPeriod(T: Int, j: Int) {

}