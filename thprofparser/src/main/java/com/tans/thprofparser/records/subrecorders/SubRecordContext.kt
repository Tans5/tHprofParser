package com.tans.thprofparser.records.subrecorders

import com.tans.thprofparser.HprofHeader

data class SubRecordContext<R : SubRecord>(
    val header: HprofHeader,
    val parentRecordTag: Int,
    val parentRecordFileOffset: Long,
    val fileOffset: Long,
    val parentBodyLen: Long,
    val subRecord: R
)