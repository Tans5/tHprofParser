package com.tans.thprofparser.records.subrecorders

import com.tans.thprofparser.ValueHolder
import com.tans.thprofparser.records.HeapDumpContext

data class PrimitiveArrayDumpContext(
    val heapDumpContext: HeapDumpContext,
    val id: Long,
    val stackTraceSerialNumber: Long,
    val type: Int,
    val elements: List<ValueHolder>
)