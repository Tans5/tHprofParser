package com.tans.thprofparser.recorders

import com.tans.thprofparser.recorders.StackFrameRecorderVisitor.Companion.StackFrameContext
import com.tans.thprofparser.writeId
import com.tans.thprofparser.writeUnsignedByte
import com.tans.thprofparser.writeUnsignedInt
import okio.BufferedSink

internal class StackFrameRecorderWriter(
    private val sink: BufferedSink
) : StackFrameRecorderVisitor() {

    private var stackFrameContext: StackFrameContext? = null

    override fun visitStackFrame(stackFrameContext: StackFrameContext) {
        this.stackFrameContext = stackFrameContext
    }

    override fun visitEnd() {
        val stackFrameContext = this.stackFrameContext
        if (stackFrameContext == null) {
            return
        }
        val recorderContext = stackFrameContext.recorderContext
        sink.writeUnsignedByte(recorderContext.tag)
        sink.writeUnsignedInt(recorderContext.timestamp)
        sink.writeUnsignedInt((recorderContext.header.identifierByteSize * 4 + 4 * 2).toLong())
        sink.writeId(stackFrameContext.id, recorderContext.header)
        sink.writeId(stackFrameContext.methodNameStringId, recorderContext.header)
        sink.writeId(stackFrameContext.methodSignatureStringId, recorderContext.header)
        sink.writeId(stackFrameContext.sourceFileNameStringId, recorderContext.header)
        sink.writeUnsignedInt(stackFrameContext.classSerialNumber)
        sink.writeUnsignedInt(stackFrameContext.lineNumber)
    }
}