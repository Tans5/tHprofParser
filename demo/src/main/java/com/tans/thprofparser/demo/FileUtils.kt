package com.tans.thprofparser.demo

import java.io.File
import java.io.RandomAccessFile
import kotlin.math.min


fun assertFileIsSame(file1: File, file2: File) {
    val file1Len = file1.length()
    val file2Len = file2.length()
    file1.inputStream().use { inputStream1 ->
        file2.inputStream().use { inputStream2 ->
            var offset = 0L
            val len = min(file2Len, file1Len)
            while (offset < len) {
                val byte1 = inputStream1.read()
                val byte2 = inputStream2.read()
                if (byte1 != byte2) {
                    throw RuntimeException("Offset=${String.format("%X", offset)}, File1=${String.format("%X", byte1)}, File2=${String.format("%X", byte2)}")
                }
                offset ++
            }
        }
    }

    if (file1Len != file2Len) {
        throw RuntimeException("File1 length is $file1Len bytes, File2 length is $file2Len bytes.")
    }
}

fun readFileBytes(file: File, start: Long, size: Long): ByteArray {
    return RandomAccessFile(file, "r").use { randomReadFile ->
        randomReadFile.seek(start)
        val bytes = ByteArray(size.toInt())
        randomReadFile.read(bytes)
        bytes
    }
}

fun assertIsSame(bytes1: ByteArray, bytes2: ByteArray, msg: String? = null) {
    val len1 = bytes1.size
    val len2 = bytes2.size
    val len = min(len1, len2)
    repeat(len) { i ->
        if (bytes1[i] != bytes2[i]) {
            throw RuntimeException("${if (msg != null) "Check $msg fail, " else "" }Index=$i, byte1=${String.format("0x%02X", bytes1[i])}, byte2=${String.format("0x%02X", bytes2[i])}")
        }
    }

    if (len1 != len2) {
        throw RuntimeException("${if (msg != null) "Check $msg fail, " else "" }Bytes1 length=$len1, Bytes2 length=$len2")
    }
    if (msg != null) {
        println("Check $msg success.")
    }
}