package com.tans.thprofparser.recorders


open class UnknownRecorderVisitor(protected val nextVisitor: UnknownRecorderVisitor? = null) {

    open fun visitBody(unknownBodyContext: UnknownBodyContext) {
        this.nextVisitor?.visitBody(unknownBodyContext)
    }

    open fun visitEnd() {
        nextVisitor?.visitEnd()
    }

    companion object {
        data class UnknownBodyContext(
            val recorderContext: RecorderContext,
            val body: ByteArray
        )
    }
}