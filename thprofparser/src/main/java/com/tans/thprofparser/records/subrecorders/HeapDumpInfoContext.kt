package com.tans.thprofparser.records.subrecorders

import com.tans.thprofparser.records.HeapDumpContext

data class HeapDumpInfoContext(
    val heapDumpContext: HeapDumpContext,
    val heapId: Long,
    val stringId: Long
)