package com.tans.thprofparser.recorders

open class LoadClassRecorderVisitor(protected val nextVisitor: LoadClassRecorderVisitor? = null) {

    open fun visitLoadClass(
        loadClassContext: LoadClassContext
    ) {
        nextVisitor?.visitLoadClass(loadClassContext)
    }

    open fun visitEnd() {
        nextVisitor?.visitEnd()
    }

    companion object {
        data class LoadClassContext(
            val recorderContext: RecorderContext,
            val classSerialNumber: Long,
            val id: Long,
            val stackTraceSerialNumber: Long,
            val classNameStringId: Long
        )
    }
}