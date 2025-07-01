package com.tans.thprofparser.recorders

import com.tans.thprofparser.HprofHeader


open class StackFrameRecorderVisitor(
    override val tag: Int,
    override val timeStamp: Long,
    override val header: HprofHeader,
    protected val nextVisitor: StackFrameRecorderVisitor? = null
) : BaseRecorderVisitor() {

    open fun visitStackFrame(
       id: Long,
       methodNameStringId: Long,
       methodSignatureStringId: Long,
       sourceFileNameStringId: Long,
       classSerialNumber: Long,
       lineNumber: Long,
    ) {
        nextVisitor?.visitStackFrame(id, methodNameStringId, methodSignatureStringId, sourceFileNameStringId, classSerialNumber, lineNumber)
    }

    open fun visitEnd() {
        nextVisitor?.visitEnd()
    }
}