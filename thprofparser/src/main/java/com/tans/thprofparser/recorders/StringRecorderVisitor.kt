package com.tans.thprofparser.recorders

import com.tans.thprofparser.HprofHeader

open class StringRecorderVisitor(
    override val tag: Int,
    override val timeStamp: Long,
    override val header: HprofHeader,
    protected val nextVisitor: StringRecorderVisitor? = null
) : BaseRecorderVisitor() {

    open fun visitString(
        id: Long,
        str: String,) {
        nextVisitor?.visitString(id, str)
    }

    open fun visitEnd() {
        nextVisitor?.visitEnd()
    }

}