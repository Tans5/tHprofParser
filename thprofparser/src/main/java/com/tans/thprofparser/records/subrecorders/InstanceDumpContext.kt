package com.tans.thprofparser.records.subrecorders

import com.tans.thprofparser.records.HeapDumpContext

data class InstanceDumpContext(
    val heapDumpContext: HeapDumpContext,
    val id: Long,
    val stackTraceSerialNumber: Long,
    val classId: Long,
    val contentBytes: ByteArray
)