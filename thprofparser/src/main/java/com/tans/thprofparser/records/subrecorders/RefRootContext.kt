package com.tans.thprofparser.records.subrecorders

import com.tans.thprofparser.records.HeapDumpContext

data class RefRootContext(
    val heapDumpContext: HeapDumpContext,
    val id: Long,
    val refId: Long
)