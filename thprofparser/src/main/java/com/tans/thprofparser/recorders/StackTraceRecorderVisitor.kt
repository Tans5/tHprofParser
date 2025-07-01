package com.tans.thprofparser.recorders

open class StackTraceRecorderVisitor(protected val nextVisitor: StackTraceRecorderVisitor? = null) {

    open fun visitStackTrace(
        stackTraceContext: StackTraceContext
    ) {
        nextVisitor?.visitStackTrace(stackTraceContext)
    }

    open fun visitEnd() {
        nextVisitor?.visitEnd()
    }

    companion object {

        data class StackTraceContext(
            val recorderContext: RecorderContext,
            val stackTraceSerialNumber: Long,
            val threadSerialNumber: Long,
            val stackFrameIds: List<Long>
        )
    }
}