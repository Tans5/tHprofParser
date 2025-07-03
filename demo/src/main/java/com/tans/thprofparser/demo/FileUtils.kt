package com.tans.thprofparser.demo

import java.io.File
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