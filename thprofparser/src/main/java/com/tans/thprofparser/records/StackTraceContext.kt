package com.tans.thprofparser.records

data class StackTraceContext(
    val recordContext: RecordContext,
    val stackTraceSerialNumber: Long,
    val threadSerialNumber: Long,
    val stackFrameIds: List<Long>
)