package com.tans.thprofparser.recorders

import com.tans.thprofparser.HprofHeader

open class LoadClassRecorderVisitor(
    override val tag: Int,
    override val timeStamp: Long,
    override val header: HprofHeader,
    protected val nextVisitor: LoadClassRecorderVisitor? = null
) : BaseRecorderVisitor() {

    open fun visitLoadClass(
        classSerialNumber: Long,
        id: Long,
        stackTraceSerialNumber: Long,
        classNameStringId: Long
    ) {
        nextVisitor?.visitLoadClass(classSerialNumber, id, stackTraceSerialNumber, classNameStringId)
    }

    open fun visitEnd() {
        nextVisitor?.visitEnd()
    }
}