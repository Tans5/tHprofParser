package com.tans.thprofparser

import com.tans.thprofparser.recorders.LoadClassRecorderVisitor
import com.tans.thprofparser.recorders.LoadClassRecorderWriter
import com.tans.thprofparser.recorders.RecorderContext
import com.tans.thprofparser.recorders.RecorderType
import com.tans.thprofparser.recorders.StackFrameRecorderVisitor
import com.tans.thprofparser.recorders.StackFrameRecorderWriter
import com.tans.thprofparser.recorders.StackTraceRecorderVisitor
import com.tans.thprofparser.recorders.StackTraceRecorderWriter
import com.tans.thprofparser.recorders.StringRecorderVisitor
import com.tans.thprofparser.recorders.StringRecorderWriter
import com.tans.thprofparser.recorders.UnknownRecorderVisitor
import com.tans.thprofparser.recorders.UnknownRecorderWriter
import com.tans.thprofparser.recorders.UnloadClassRecorderVisitor
import com.tans.thprofparser.recorders.UnloadClassRecorderWriter
import okio.buffer
import okio.sink
import java.io.OutputStream

class HprofWriter(outputStream: OutputStream) : HprofVisitor() {

    private val sink = outputStream.sink().buffer()

    override fun visit(header: HprofHeader) {
        sink.writeString(header.version.versionString, Charsets.UTF_8)
        sink.writeByte(0)
        sink.writeInt(header.identifierByteSize)
        sink.writeLong(header.heapDumpTimestamp)
    }

    override fun visitStringRecorder(
        recorderContext: RecorderContext
    ): StringRecorderVisitor? {
        return StringRecorderWriter(sink)
    }

    override fun visitLoadClassRecorder(
        recorderContext: RecorderContext
    ): LoadClassRecorderVisitor? {
        return LoadClassRecorderWriter(sink)
    }

    override fun visitUnloadClassRecorder(
        recorderContext: RecorderContext
    ): UnloadClassRecorderVisitor? {
        return UnloadClassRecorderWriter(sink)
    }

    override fun visitStackFrameRecorder(
        recorderContext: RecorderContext
    ): StackFrameRecorderVisitor? {
        return StackFrameRecorderWriter(sink)
    }

    override fun visitStackTraceRecorder(
        recorderContext: RecorderContext
    ): StackTraceRecorderVisitor? {
        return StackTraceRecorderWriter(sink)
    }

    override fun visitUnknownRecorder(
        recorderContext: RecorderContext
    ): UnknownRecorderVisitor? {
        return UnknownRecorderWriter(sink)
    }


    override fun visitEnd() {
        sink.writeUnsignedByte(RecorderType.HEAP_DUMP_END.tag)
        sink.writeUnsignedInt(0L)
        sink.writeUnsignedInt(0L)
        sink.flush()
    }
}