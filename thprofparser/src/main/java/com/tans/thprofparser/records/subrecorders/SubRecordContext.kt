package com.tans.thprofparser.records.subrecorders

data class SubRecordContext<R : SubRecord>(
    val parentRecordTag: Int,
    val parentRecordFileOffset: Long,
    val fileOffset: Long,
    val subRecordBodyLen: Long,
    val subRecord: R
)