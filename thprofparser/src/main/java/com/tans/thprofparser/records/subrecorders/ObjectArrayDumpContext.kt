package com.tans.thprofparser.records.subrecorders

import com.tans.thprofparser.records.HeapDumpContext

data class ObjectArrayDumpContext(
    val heapDumpContext: HeapDumpContext,
    val id: Long,
    val stackTraceSerialNumber: Long,
    val arrayClassId: Long,
    val elementIds: List<Long>
)