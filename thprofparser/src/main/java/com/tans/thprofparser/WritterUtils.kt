package com.tans.thprofparser

import okio.BufferedSink

private const val BYTE_MASK = 0x00_00_00_ff

private const val SHORT_MASK = 0x00_00_ff_ff

private const val INT_MASK = 0x00_00_00_00_ff_ff_ff_ffL

fun BufferedSink.writeUnsignedByte(byte: Int) {
    writeByte(byte and BYTE_MASK)
}

fun BufferedSink.writeUnsignedShort(short: Int) {
    writeShort(short and SHORT_MASK)
}

fun BufferedSink.writeUnsignedInt(int: Long) {
    writeInt((int and INT_MASK).toInt())
}

fun BufferedSink.writeId(id: Long, header: HprofHeader) {
    when (header.identifierByteSize) {
        1 -> {
            writeUnsignedByte(id.toInt())
        }
        2 -> {
            writeUnsignedShort(id.toInt())
        }
        4 -> {
            writeUnsignedInt(id)
        }
        8 -> {
            writeLong(id)
        }
        else -> {
            throw HprofParserException("Can not write ${header.identifierByteSize} length id.")
        }
    }
}