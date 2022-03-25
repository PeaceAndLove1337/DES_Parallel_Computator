package computationStrategies

import extensionFunctions.setAllItemsSinceIndex
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import javax.crypto.Cipher

/**
 * Реализация параллельного вычислителя на основе котлин корутин
 */
class CoroutinesComputationStrategy : ParallelComputationStrategy {

    @OptIn(DelicateCoroutinesApi::class)
    override fun encode(
        encryptor: Cipher,
        bytesToEncode: ByteArray,
        countOfThreads: Int,
        countOfBytesPerThread: Int
    ): ByteArray {
        val threadPoolContext = Executors.newFixedThreadPool(countOfThreads).asCoroutineDispatcher()
        return doParallelComputation(encryptor, bytesToEncode, countOfBytesPerThread, threadPoolContext)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun decode(
        decryptor: Cipher,
        bytesToDecode: ByteArray,
        countOfThreads: Int,
        countOfBytesPerThread: Int
    ): ByteArray {
        val threadPoolContext = Executors.newFixedThreadPool(countOfThreads).asCoroutineDispatcher()
        return doParallelComputation(decryptor, bytesToDecode, countOfBytesPerThread, threadPoolContext)
    }

    /**
     * Основной метод использующийся для параллельного шифрования/расшифрования по алгоритму DES
     * В методе используется runBlocking-билдер для блокировки основного потока на время вычислений
     * корутин внутри него. Непосредственно в цикле создаются новые корутина, главная цель которых
     * произвести шифрование/дешифрование, а так же вставить результат в нужный блок массива
     *
     * @param cipher сущность для шифрования/дешифрования
     * @param bytesToCompute входные байты для шифрования/дешифрования
     * @param countOfBytesPerThread кол-во обрабатываемых байт на один поток
     * @param coroutineDispatcher диспатчер
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun doParallelComputation(
        cipher: Cipher,
        bytesToCompute: ByteArray,
        countOfBytesPerThread: Int,
        coroutineDispatcher: ExecutorCoroutineDispatcher,
    ): ByteArray = runBlocking {
        val result = ByteArray(bytesToCompute.size)
        val countOfTasks = bytesToCompute.size / countOfBytesPerThread

        for (i in 0 until countOfTasks) {

            val indexOfFirstIndexOfTaskBlock = i * countOfBytesPerThread
            val currentBytesSlice = bytesToCompute.sliceArray(
                indexOfFirstIndexOfTaskBlock until (i + 1) * countOfBytesPerThread
            )

            launch (coroutineDispatcher) {
                result.setAllItemsSinceIndex(indexOfFirstIndexOfTaskBlock,  cipher.doFinal(currentBytesSlice))
            }
        }

        coroutineDispatcher.close()

        return@runBlocking result
    }

}