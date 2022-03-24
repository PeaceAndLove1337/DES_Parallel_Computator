import computationStrategies.ParallelComputationStrategy
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * Сущность для шифрования/расшифрования некоторой информации на основе
 * криптоалгоритма DES в режиме кодовой книги. Генерация и применение
 * секретного ключа инкапсулировано в класс
 *
 *
 * @param secretKey секретный ключ на основе которого происходит шифрование/дешифрование.
 * По умолчанию генерируется случайный секретный ключы.
 *
 * @author Pavlov Ivan
 * @since 22.03.22
 */
class DesComputator(
    private val secretKey: SecretKey = KeyGenerator.getInstance("DES").generateKey()
) {

    private val encryptor: Cipher = Cipher.getInstance("DES/ECB/NoPadding")
    private val decryptor: Cipher = Cipher.getInstance("DES/ECB/NoPadding")

    init {
        encryptor.init(Cipher.ENCRYPT_MODE, secretKey)
        decryptor.init(Cipher.DECRYPT_MODE, secretKey)
    }

    /**
     * Метод реализующий параллельное шифрование по алгоритму DES
     * @param bytesToEncode входные байты для шифрования
     * @param countOfThreads количество потоков для обработки
     * @param countOfBytesPerThread количество обрабатываемых байт на поток (не путать с количеством байт
     * на один блок шифрования, который для DES равен 8 байтам) Соответственно, на каждом потоке в случае,
     * если countOfBytesPerThread будет > 8 будет обрабатываться больше одного блока в терминах криптоалгоритма DES.
     * Данный "маневр" сделан в виду того, что кидать на обработку треду по 8 байт вероятнее всего нецелесообразно,
     * тк куда больше вермени будет потрачено на переключение контекста и распределение задач на треды.
     * @param computationStrategy стратегия параллельного вычисления
     */
    fun encryptParallel(
        bytesToEncode: ByteArray,
        countOfThreads: Int,
        countOfBytesPerThread: Int,
        computationStrategy: ParallelComputationStrategy
    ): ByteArray =
        computationStrategy.encode(encryptor, bytesToEncode, countOfThreads, countOfBytesPerThread)


    /**
     * Метод реализующий параллельное дешифрование по алгоритму DES
     * Документация по параметрам аналогична методу [encryptParallel]
     */
    fun decryptParallel(
        bytesToEncode: ByteArray,
        countOfThreads: Int,
        countOfBytesPerThread: Int,
        computationStrategy: ParallelComputationStrategy
    ): ByteArray =
        computationStrategy.decode(decryptor, bytesToEncode, countOfThreads, countOfBytesPerThread)


    /**
     * Зашифровать входной поток байт однопоточно по алгоритму DES
     */
    fun encryptNotParallel(bytesToEncode: ByteArray): ByteArray =
        encryptor.doFinal(bytesToEncode)

    /**
     * Расшифровать входной поток байт однопоточно по алгоритму DES
     */
    fun decryptNotParallel(bytesToEncode: ByteArray): ByteArray =
        decryptor.doFinal(bytesToEncode)

    fun getSecretKeyBytes() = secretKey.encoded
}
