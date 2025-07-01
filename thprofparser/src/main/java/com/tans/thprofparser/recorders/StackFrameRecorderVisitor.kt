package com.tans.thprofparser.recorders

open class StackFrameRecorderVisitor(
    protected val nextVisitor: StackFrameRecorderVisitor? = null
) {

    open fun visitStackFrame(
       stackFrameContext: StackFrameContext
    ) {
        nextVisitor?.visitStackFrame(stackFrameContext)
    }

    open fun visitEnd() {
        nextVisitor?.visitEnd()
    }

    companion object {
        data class StackFrameContext(
            val recorderContext: RecorderContext,
            val id: Long,
            val methodNameStringId: Long,
            val methodSignatureStringId: Long,
            val sourceFileNameStringId: Long,
            val classSerialNumber: Long,
            val lineNumber: Long,
        )
    }
}