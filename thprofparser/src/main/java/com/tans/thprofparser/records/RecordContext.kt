package com.tans.thprofparser.records

import com.tans.thprofparser.HprofHeader

data class RecordContext<R : Record>(
    val header: HprofHeader,
    val fileOffset: Long,
    val record: R
)