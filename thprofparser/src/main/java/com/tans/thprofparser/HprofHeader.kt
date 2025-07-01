package com.tans.thprofparser

data class HprofHeader(
    val version: HprofVersion,
    val identifierByteSize: Int,
    val heapDumpTimestamp: Long
)
