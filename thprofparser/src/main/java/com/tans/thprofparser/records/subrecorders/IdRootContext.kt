package com.tans.thprofparser.records.subrecorders

import com.tans.thprofparser.records.HeapDumpContext

data class IdRootContext(
    val heapDumpContext: HeapDumpContext,
    val id: Long
)