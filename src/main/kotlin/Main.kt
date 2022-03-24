import computationStrategies.CoroutinesComputationStrategy
import computationStrategies.CompletableFutureComputationStrategy
import java.nio.file.Files
import java.nio.file.Paths


fun main() {
    val countOfBytesPerEncryptionBlock = 1024 * 1024
    val countOfThreads = 5
    val fileBytes = getBytesOfFile()

    val computator = DesComputator()
    val cfStrategy = CompletableFutureComputationStrategy()
    val coroutineStrategy = CoroutinesComputationStrategy()

    val resOfEncryption = computeWithTime("non parallel encryption time :"){
        computator.encryptNotParallel(fileBytes)
    }
    computeWithTime("completable future encoding time:"){
        computator.encryptParallel(fileBytes, countOfThreads,
            countOfBytesPerEncryptionBlock, cfStrategy)
    }
    computeWithTime("coroutine  encoding time:"){
        computator.encryptParallel(fileBytes, countOfThreads,
            countOfBytesPerEncryptionBlock, coroutineStrategy)
    }
    computeWithTime("non parallel decryption time :"){
        computator.encryptNotParallel(resOfEncryption)
    }
    computeWithTime("completable future decoding time:"){
        computator.decryptParallel(resOfEncryption, countOfThreads,
            countOfBytesPerEncryptionBlock, cfStrategy)
    }
    computeWithTime("coroutine  decoding time:"){
        computator.encryptParallel(resOfEncryption, countOfThreads,
            countOfBytesPerEncryptionBlock, coroutineStrategy)
    }

    println()

}

/**
 * Получить байты файла
 */
private fun getBytesOfFile(path: String = "fileToEncoding/fileBlob"): ByteArray {
    val filePath = Paths.get(path)
    return Files.readAllBytes(filePath)
}

/**
 * Выполнить блок кода и вывести время выполнения в секундах
 */
private fun <T> computeWithTime(timeMessage:String, codeBlock: ()->T): T{
    val timeBefore = System.currentTimeMillis()
    val result = codeBlock.invoke()
    val timeAfter = System.currentTimeMillis()
    println("$timeMessage : ${(timeAfter-timeBefore).toDouble()/1000}")
    return result
}

