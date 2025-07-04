package com.tans.thprofparser

sealed class ValueHolder() {
    abstract val size: Int
    data class ReferenceHolder(val value: Long, override val size: Int) : ValueHolder()
    data class BooleanHolder(val value: Boolean, override val size: Int = 1) : ValueHolder()
    data class CharHolder(val value: Char, override val size: Int = 2) : ValueHolder()
    data class FloatHolder(val value: Float, override val size: Int = 4) : ValueHolder()
    data class DoubleHolder(val value: Double, override val size: Int = 8) : ValueHolder()
    data class ByteHolder(val value: Byte, override val size: Int = 1) : ValueHolder()
    data class ShortHolder(val value: Short, override val size: Int = 2) : ValueHolder()
    data class IntHolder(val value: Int, override val size: Int = 4) : ValueHolder()
    data class LongHolder(val value: Long, override val size: Int = 8) : ValueHolder()
}