package extensionFunctions


/**
 * Вставить массив байт в другой массив байт начиная с определенного индекса
 * @param index индекс, с которого нужно начать вставку
 * @param items массив байт который необходимо вставить
 */
fun ByteArray.setAllItemsSinceIndex(index: Int, items: ByteArray) {
    for (i in items.indices) {
        this[i + index] = items[i]
    }
}