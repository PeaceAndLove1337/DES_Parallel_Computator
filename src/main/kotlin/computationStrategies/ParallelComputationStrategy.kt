package computationStrategies

import javax.crypto.Cipher

/**
 * Интерфейс стратегии параллельного шифрования/дешифрования
 */
interface ParallelComputationStrategy {

    fun encode(encryptor: Cipher, bytesToEncode: ByteArray, countOfThreads: Int, countOfBytesPerThread: Int): ByteArray

    fun decode(decryptor: Cipher, bytesToDecode: ByteArray, countOfThreads: Int, countOfBytesPerThread: Int): ByteArray
}