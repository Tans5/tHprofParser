package com.tans.thprofparser.recorders

import com.tans.thprofparser.HprofHeader

open class StackTraceRecorderVisitor(
    override val tag: Int,
    override val timeStamp: Long,
    override val header: HprofHeader,
    protected val nextVisitor: StackTraceRecorderVisitor? = null) : BaseRecorderVisitor() {

    open fun visitStackTrace(
        stackTraceSerialNumber: Long,
        threadSerialNumber: Long,
        stackFrameIds: List<Long>
    ) {
        nextVisitor?.visitStackTrace(stackTraceSerialNumber, threadSerialNumber, stackFrameIds)
    }

    open fun visitEnd() {
        nextVisitor?.visitEnd()
    }
}