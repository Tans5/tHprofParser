package com.tans.thprofparser.recorders

import com.tans.thprofparser.HprofHeader

open class UnknownRecorderVisitor(
    override val tag: Int,
    override val timeStamp: Long,
    override val header: HprofHeader,
    protected val nextVisitor: UnknownRecorderVisitor? = null
) : BaseRecorderVisitor() {

    open fun visitBody(body: ByteArray) {
        this.nextVisitor?.visitBody(body)
    }

    open fun visitEnd() {
        nextVisitor?.visitEnd()
    }
}