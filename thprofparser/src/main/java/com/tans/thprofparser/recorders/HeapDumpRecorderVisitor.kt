package com.tans.thprofparser.recorders

import com.tans.thprofparser.HprofHeader

open class HeapDumpRecorderVisitor(protected val nextVisitor: HeapDumpRecorderVisitor? = null) {

    open fun visit(
        tag: Byte,
        timeStamp: Int,
        body: ByteArray,
        hprofHeader: HprofHeader) {
        nextVisitor?.visit(tag, timeStamp, body, hprofHeader)
    }

    open fun visitEnd() {
        nextVisitor?.visitEnd()
    }
}