package com.tans.thprofparser.recorders

import com.tans.thprofparser.HprofHeader

data class RecorderContext(
    val tag: Int,
    val timestamp: Long,
    val bodyLength: Long,
    val header: HprofHeader
)