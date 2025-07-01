package com.tans.thprofparser.recorders


open class UnloadClassRecorderVisitor(protected val nextVisitor: UnloadClassRecorderVisitor? = null) {

    private var unloadClassContext: UnloadClassContext? = null

    open fun visitUnloadClassRecorder(unloadClassContext: UnloadClassContext) {
        this.unloadClassContext
    }

    open fun visitEnd() {
        nextVisitor?.visitEnd()
    }

    companion object {
        data class UnloadClassContext(
            val recorderContext: RecorderContext,
            val classSerialNumber: Long
        )
    }
}