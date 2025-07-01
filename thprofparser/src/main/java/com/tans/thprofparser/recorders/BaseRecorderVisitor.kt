package com.tans.thprofparser.recorders

import com.tans.thprofparser.HprofHeader

abstract class BaseRecorderVisitor(
    ) {
    abstract val tag: Int
    abstract val timeStamp: Long
    abstract val header: HprofHeader
}