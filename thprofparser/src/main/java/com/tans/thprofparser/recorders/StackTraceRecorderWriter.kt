package com.tans.thprofparser.recorders

import com.tans.thprofparser.recorders.StackTraceRecorderVisitor.Companion.StackTraceContext
import com.tans.thprofparser.writeId
import com.tans.thprofparser.writeUnsignedByte
import com.tans.thprofparser.writeUnsignedInt
import okio.BufferedSink

internal class StackTraceRecorderWriter(private val sink: BufferedSink) : StackTraceRecorderVisitor() {

    private var stackTraceContext: StackTraceContext? = null

    override fun visitStackTrace(stackTraceContext: StackTraceContext) {
        this.stackTraceContext = stackTraceContext
    }

    override fun visitEnd() {
        val stackTraceContext = this.stackTraceContext
        if (stackTraceContext == null) {
            return
        }
        val recorderContext = stackTraceContext.recorderContext
        sink.writeUnsignedByte(recorderContext.tag)
        sink.writeUnsignedInt(recorderContext.timestamp)
        sink.writeUnsignedInt((stackTraceContext.stackFrameIds.size * recorderContext.header.identifierByteSize + 4 * 3).toLong())
        sink.writeUnsignedInt(stackTraceContext.stackTraceSerialNumber)
        sink.writeUnsignedInt(stackTraceContext.threadSerialNumber)
        sink.writeUnsignedInt(stackTraceContext.stackFrameIds.size.toLong())
        for (id in stackTraceContext.stackFrameIds) {
            sink.writeId(id, recorderContext.header)
        }
    }
}