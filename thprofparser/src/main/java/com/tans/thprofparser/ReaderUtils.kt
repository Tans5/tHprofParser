package com.tans.thprofparser

import okio.BufferedSource
import java.nio.charset.Charset


private const val INT_MASK = 0xffffffffL
private const val BYTE_MASK = 0xff
private const val SHORT_MASK = 0xffff

fun BufferedSource.readUnsignedByte(): Int {
    return readByte().toUnsignedByte()
}

fun Byte.toUnsignedByte(): Int {
    return this.toInt() and BYTE_MASK
}

fun BufferedSource.readUnsignedShort(): Int {
    return readShort().toUnsignedShort()
}

fun Short.toUnsignedShort(): Int {
    return this.toInt() and SHORT_MASK
}

fun BufferedSource.readUnsignedInt(): Long {
    return readInt().toUnsignedInt()
}

fun Int.toUnsignedInt(): Long {
    return this.toLong() and INT_MASK
}

fun BufferedSource.readId(header: HprofHeader): Long {
    return when (header.identifierByteSize) {
        1 -> readByte().toLong()
        2 -> readShort().toLong()
        4 -> readInt().toLong()
        8 -> readLong()
        else -> throw IllegalArgumentException("ID Length must be 1, 2, 4, or 8")
    }
}

fun BufferedSource.readBoolean(): Boolean {
    return readByte()
        .toInt() != 0
}

fun BufferedSource.readString(
    byteCount: Int,
    charset: Charset
): String {
    return readString(byteCount.toLong(), charset)
}

fun BufferedSource.readChar(): Char {
    return readString(2, Charsets.UTF_16BE)[0]
}

fun BufferedSource.readFloat(): Float {
    return Float.fromBits(readInt())
}

fun BufferedSource.readDouble(): Double {
    return Double.fromBits(readLong())
}

