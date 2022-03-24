package computationStrategies

import extensionFunctions.setAllItemsSinceIndex
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher


/**
 * Реализация параллельного вычислителя на основе CompletableFuture из java.util.concurrent
 */
class CompletableFutureComputationStrategy : ParallelComputationStrategy {

    override fun encode(
        encryptor: Cipher,
        bytesToEncode: ByteArray,
        countOfThreads: Int,
        countOfBytesPerThread: Int
    ): ByteArray {
        val threadPool = Executors.newFixedThreadPool(countOfThreads)
        return doParallelComputation(encryptor, bytesToEncode, countOfBytesPerThread, threadPool)
    }

    override fun decode(
        decryptor: Cipher,
        bytesToDecode: ByteArray,
        countOfThreads: Int,
        countOfBytesPerThread: Int
    ): ByteArray {
        val threadPool = Executors.newFixedThreadPool(countOfThreads)
        return doParallelComputation(decryptor, bytesToDecode, countOfBytesPerThread, threadPool)
    }

    /**
     * Основной метод использующийся для параллельного шифрования/расшифрования по алгоритму DES
     * Для параллелизации используется фиксированный пул потоков + выполнение задач на основе CompletableFuture
     * @param cipher сущность для шифрования/дешифрования
     * @param bytesToCompute входные байты для шифрования/дешифрования
     * @param countOfBytesPerThread кол-во обрабатываемых байт на один поток
     * @param threadPool пул потоков на которых происходят вычисления
     */
    private fun doParallelComputation(
        cipher: Cipher,
        bytesToCompute: ByteArray,
        countOfBytesPerThread: Int,
        threadPool: ExecutorService,
    ): ByteArray {
        val result = ByteArray(bytesToCompute.size)
        val countOfTasks = bytesToCompute.size / countOfBytesPerThread

        for (i in 0 until countOfTasks) {
            val indexOfFirstIndexOfTaskBlock = i * countOfBytesPerThread
            val currentBytesSlice = bytesToCompute.sliceArray(
                indexOfFirstIndexOfTaskBlock until (i + 1) * countOfBytesPerThread
            )

            CompletableFuture.supplyAsync({
                    cipher.doFinal(currentBytesSlice)
                },
                threadPool
            ).thenAccept {
                result.setAllItemsSinceIndex(indexOfFirstIndexOfTaskBlock, it)
            }

        }

        threadPool.shutdown()
        val isSuccessful = threadPool.awaitTermination(120, TimeUnit.SECONDS)
        if (!isSuccessful)
            throw Exception("Время ожидания превышено. Произошла непредвиденная ошибка.")

        return result
    }
}
