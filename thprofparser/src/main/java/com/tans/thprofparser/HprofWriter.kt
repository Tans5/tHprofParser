package com.tans.thprofparser

import com.tans.thprofparser.recorders.LoadClassRecorderVisitor
import com.tans.thprofparser.recorders.LoadClassRecorderWriter
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
        tag: Int,
        timeStamp: Long,
        header: HprofHeader
    ): StringRecorderVisitor? {
        return StringRecorderWriter(
            tag = tag,
            timeStamp = timeStamp,
            header = header,
            sink = sink
        )
    }

    override fun visitLoadClassRecorder(
        tag: Int,
        timeStamp: Long,
        header: HprofHeader
    ): LoadClassRecorderVisitor? {
        return LoadClassRecorderWriter(
            tag = tag,
            timeStamp = timeStamp,
            header = header,
            sink = sink
        )
    }

    override fun visitUnloadClassRecorder(
        tag: Int,
        timeStamp: Long,
        header: HprofHeader
    ): UnloadClassRecorderVisitor? {
        return UnloadClassRecorderWriter(
            tag = tag,
            timeStamp = timeStamp,
            header = header,
            sink = sink
        )
    }

    override fun visitStackFrameRecorder(
        tag: Int,
        timeStamp: Long,
        header: HprofHeader
    ): StackFrameRecorderVisitor? {
        return StackFrameRecorderWriter(
            tag = tag,
            timeStamp = timeStamp,
            header = header,
            sink = sink
        )
    }

    override fun visitStackTraceRecorder(
        tag: Int,
        timeStamp: Long,
        header: HprofHeader
    ): StackTraceRecorderVisitor? {
        return StackTraceRecorderWriter(
            tag = tag,
            timeStamp = timeStamp,
            header = header,
            sink = sink
        )
    }

    override fun visitUnknownRecorder(
        tag: Int,
        timeStamp: Long,
        header: HprofHeader
    ): UnknownRecorderVisitor? {
        return UnknownRecorderWriter(
            tag = tag,
            timeStamp = timeStamp,
            header = header,
            sink = sink
        )
    }


    override fun visitEnd() {
        sink.writeUnsignedByte(RecorderType.HEAP_DUMP_END.tag)
        sink.writeUnsignedInt(0L)
        sink.writeUnsignedInt(0L)
    }
}