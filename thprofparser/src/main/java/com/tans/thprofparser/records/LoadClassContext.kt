package com.tans.thprofparser.records

data class LoadClassContext(
    val recordContext: RecordContext,
    val classSerialNumber: Long,
    val id: Long,
    val stackTraceSerialNumber: Long,
    val classNameStringId: Long
)