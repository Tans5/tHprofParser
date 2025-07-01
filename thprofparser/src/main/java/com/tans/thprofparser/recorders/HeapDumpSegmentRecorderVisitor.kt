package com.tans.thprofparser.recorders

import com.tans.thprofparser.HprofHeader

open class HeapDumpSegmentRecorderVisitor(protected val nextVisitor: HeapDumpRecorderVisitor? = null) {

    open fun visit(
        tag: Byte,
        timeStamp: Int,

        body: ByteArray,

        header: HprofHeader) {
        nextVisitor?.visit(tag, timeStamp, body, header)
    }

    open fun visitEnd() {
        nextVisitor?.visitEnd()
    }
}