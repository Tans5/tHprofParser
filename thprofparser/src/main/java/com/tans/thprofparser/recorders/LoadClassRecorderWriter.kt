package com.tans.thprofparser.recorders

import com.tans.thprofparser.recorders.LoadClassRecorderVisitor.Companion.LoadClassContext
import com.tans.thprofparser.writeId
import com.tans.thprofparser.writeUnsignedByte
import com.tans.thprofparser.writeUnsignedInt
import okio.BufferedSink

internal class LoadClassRecorderWriter(
    private val sink: BufferedSink
) : LoadClassRecorderVisitor() {

    private var loadClassContext: LoadClassContext? = null


    override fun visitLoadClass(loadClassContext: LoadClassContext) {
        this.loadClassContext = loadClassContext
    }

    override fun visitEnd() {
        val loadClassContext = this.loadClassContext
        if (loadClassContext == null) {
            return
        }
        val recorderContext = loadClassContext.recorderContext
        sink.writeUnsignedByte(recorderContext.tag)
        sink.writeUnsignedInt(recorderContext.timestamp)
        sink.writeInt(4 + recorderContext.header.identifierByteSize + 4 + recorderContext.header.identifierByteSize)
        sink.writeUnsignedInt(loadClassContext.classSerialNumber)
        sink.writeId(loadClassContext.id, recorderContext.header)
        sink.writeUnsignedInt(loadClassContext.stackTraceSerialNumber)
        sink.writeId(loadClassContext.classNameStringId, recorderContext.header)
    }
}