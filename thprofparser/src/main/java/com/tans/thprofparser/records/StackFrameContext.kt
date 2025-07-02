package com.tans.thprofparser.records

data class StackFrameContext(
    val recordContext: RecordContext,
    val id: Long,
    val methodNameStringId: Long,
    val methodSignatureStringId: Long,
    val sourceFileNameStringId: Long,
    val classSerialNumber: Long,
    val lineNumber: Long,
)