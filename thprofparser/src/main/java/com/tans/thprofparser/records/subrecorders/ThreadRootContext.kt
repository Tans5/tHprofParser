package com.tans.thprofparser.records.subrecorders

import com.tans.thprofparser.records.HeapDumpContext

data class ThreadRootContext(
    val heapDumpContext: HeapDumpContext,
    val id: Long,
    val threadSerialNumber: Long,
)