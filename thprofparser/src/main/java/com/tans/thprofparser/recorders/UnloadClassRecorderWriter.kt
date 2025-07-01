package com.tans.thprofparser.recorders

import com.tans.thprofparser.recorders.UnloadClassRecorderVisitor.Companion.UnloadClassContext
import com.tans.thprofparser.writeUnsignedByte
import com.tans.thprofparser.writeUnsignedInt
import okio.BufferedSink

internal class UnloadClassRecorderWriter(private val sink: BufferedSink) : UnloadClassRecorderVisitor() {

    private var unloadClassContext: UnloadClassContext? = null

    override fun visitUnloadClassRecorder(unloadClassContext: UnloadClassContext) {
        this.unloadClassContext = unloadClassContext
    }

    override fun visitEnd() {
        val unloadClassContext = this.unloadClassContext
        if (unloadClassContext == null) {
            return
        }
        val recorderContext = unloadClassContext.recorderContext
        sink.writeUnsignedByte(recorderContext.tag)
        sink.writeUnsignedInt(recorderContext.timestamp)
        sink.writeUnsignedInt(4)
        sink.writeUnsignedInt(unloadClassContext.classSerialNumber)
    }
}