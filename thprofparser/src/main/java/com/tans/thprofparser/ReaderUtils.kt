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

fun BufferedSource.readValue(type: Int, header: HprofHeader): ValueHolder {
    return when (type) {
        REFERENCE_HPROF_TYPE -> {
            ValueHolder.ReferenceHolder(readId(header), header.identifierByteSize)
        }
        PrimitiveType.BOOLEAN.hprofType -> {
            ValueHolder.BooleanHolder(readBoolean())
        }

        PrimitiveType.CHAR.hprofType -> {
            ValueHolder.CharHolder(readChar())
        }

        PrimitiveType.FLOAT.hprofType -> {
            ValueHolder.FloatHolder(readFloat())
        }

        PrimitiveType.DOUBLE.hprofType -> {
            ValueHolder.DoubleHolder(readDouble())
        }

        PrimitiveType.BYTE.hprofType -> {
            ValueHolder.ByteHolder(readByte())
        }

        PrimitiveType.SHORT.hprofType -> {
            ValueHolder.ShortHolder(readShort())
        }

        PrimitiveType.INT.hprofType -> {
            ValueHolder.IntHolder(readInt())
        }

        PrimitiveType.LONG.hprofType -> {
            ValueHolder.LongHolder(readLong())
        }

        else -> {
            throw HprofParserException("Unknown value type: $type")
        }
    }
}

fun BufferedSource.readValue(header: HprofHeader): ValueHolder {
    val type = readUnsignedByte()
    return readValue(type, header)
}

fun BufferedSource.readConstField(header: HprofHeader): ConstField {
    return ConstField(
        index = readUnsignedInt(),
        value = readValue(header)
    )
}

fun BufferedSource.readStaticField(header: HprofHeader): StaticField {
    return StaticField(
        fieldNameStrId = readId(header),
        value = readValue(header)
    )
}

fun BufferedSource.readMemberField(header: HprofHeader): MemberField {
    return MemberField(
        fieldNameStrId = readId(header),
        type = readUnsignedByte()
    )
}

