package com.tans.thprofparser.recorders


open class StringRecorderVisitor(protected val nextVisitor: StringRecorderVisitor? = null) {

    open fun visitString(stringContext: StringContext) {
        nextVisitor?.visitString(stringContext)
    }

    open fun visitEnd() {
        nextVisitor?.visitEnd()
    }

    companion object {
        data class StringContext(
            val recorderContext: RecorderContext,
            val id: Long,
            val str: String
        )
    }
}