package com.tans.thprofparser.recorders

import com.tans.thprofparser.HprofHeader

open class UnloadClassRecorderVisitor(
    override val tag: Int,
    override val timeStamp: Long,
    override val header: HprofHeader,
    protected val nextVisitor: UnloadClassRecorderVisitor? = null
) : BaseRecorderVisitor() {

    open fun visitUnloadClassRecorder(classSerialNumber: Long) {
        nextVisitor?.visitUnloadClassRecorder(classSerialNumber)
    }

    open fun visitEnd() {
        nextVisitor?.visitEnd()
    }
}