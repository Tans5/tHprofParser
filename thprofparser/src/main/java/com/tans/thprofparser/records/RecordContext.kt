package com.tans.thprofparser.records

import com.tans.thprofparser.HprofHeader

data class RecordContext(
    val tag: Int,
    val timestamp: Long,
    val bodyLength: Long,
    val header: HprofHeader
)