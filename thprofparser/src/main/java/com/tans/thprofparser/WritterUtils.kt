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

fun BufferedSink.writeBoolean(value: Boolean) {
    writeByte(if (value) 0x01 else 0x00)
}

fun BufferedSink.writeChar(value: Char) {
    writeUnsignedShort(value.code)
}

fun BufferedSink.writeFloat(value: Float) {
    writeInt(value.toRawBits())
}

fun BufferedSink.writeDouble(value: Double) {
    writeLong(value.toRawBits())
}

fun BufferedSink.writeValue(value: ValueHolder, header: HprofHeader, writeType: Boolean) {
    when (value) {
        is ValueHolder.BooleanHolder ->  {
            if (writeType) {
                writeUnsignedByte(PrimitiveType.BOOLEAN.hprofType)
            }
            writeBoolean(value.value)
        }
        is ValueHolder.ByteHolder -> {
            if (writeType) {
                writeUnsignedByte(PrimitiveType.BYTE.hprofType)
            }
            writeUnsignedByte(value.value.toInt())
        }
        is ValueHolder.CharHolder -> {
            if (writeType) {
                writeUnsignedByte(PrimitiveType.CHAR.hprofType)
            }
            writeChar(value.value)
        }
        is ValueHolder.DoubleHolder ->  {
            if (writeType) {
                writeUnsignedByte(PrimitiveType.DOUBLE.hprofType)
            }
            writeDouble(value.value)
        }
        is ValueHolder.FloatHolder -> {
            if (writeType) {
                writeUnsignedByte(PrimitiveType.FLOAT.hprofType)
            }
            writeFloat(value.value)
        }
        is ValueHolder.IntHolder ->  {
            if (writeType) {
                writeUnsignedByte(PrimitiveType.INT.hprofType)
            }
            writeInt(value.value)
        }
        is ValueHolder.LongHolder ->  {
            if (writeType) {
                writeUnsignedByte(PrimitiveType.LONG.hprofType)
            }
            writeLong(value.value)
        }
        is ValueHolder.ReferenceHolder -> {
            if (writeType) {
                writeUnsignedByte(REFERENCE_HPROF_TYPE)
            }
            writeId(value.value, header)
        }
        is ValueHolder.ShortHolder -> {
            if (writeType) {
                writeUnsignedByte(PrimitiveType.SHORT.hprofType)
            }
            writeShort(value.value.toInt())
        }
    }
}

fun BufferedSink.writeConstField(constField: ConstField, header: HprofHeader) {
    writeUnsignedInt(constField.index)
    writeValue(constField.value, header, true)
}

fun BufferedSink.writeStaticField(staticField: StaticField, header: HprofHeader) {
    writeId(staticField.fieldNameStrId, header)
    writeValue(staticField.value, header, true)
}

fun BufferedSink.writeMemberField(memberField: MemberField, header: HprofHeader) {
    writeId(memberField.fieldNameStrId, header)
    writeUnsignedByte(memberField.type)
}