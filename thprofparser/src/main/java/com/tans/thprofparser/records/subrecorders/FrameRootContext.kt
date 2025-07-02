package com.tans.thprofparser.records.subrecorders

import com.tans.thprofparser.records.HeapDumpContext

data class FrameRootContext(
    val heapDumpContext: HeapDumpContext,
    val id: Long,
    val threadSerialNumber: Long,
    val frameNumber: Long
)
