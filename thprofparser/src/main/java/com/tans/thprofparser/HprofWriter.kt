package com.tans.thprofparser

import com.tans.thprofparser.records.HeapDumpRecordVisitor
import com.tans.thprofparser.records.HeapDumpRecordWriter
import com.tans.thprofparser.records.LoadClassContext
import com.tans.thprofparser.records.RecordContext
import com.tans.thprofparser.records.RecordType
import com.tans.thprofparser.records.StackFrameContext
import com.tans.thprofparser.records.StackTraceContext
import com.tans.thprofparser.records.StringContext
import com.tans.thprofparser.records.UnknownContext
import com.tans.thprofparser.records.UnloadClassContext
import okio.buffer
import okio.sink
import java.io.OutputStream

class HprofWriter(outputStream: OutputStream) : HprofVisitor() {

    private val sink = outputStream.sink().buffer()

    override fun visitHeader(header: HprofHeader) {
        sink.writeString(header.version.versionString, Charsets.UTF_8)
        sink.writeByte(0)
        sink.writeInt(header.identifierByteSize)
        sink.writeLong(header.heapDumpTimestamp)
    }

    override fun visitStringRecord(context: StringContext) {
        val header = context.recordContext.header
        val bodyBytes = context.str.toByteArray(Charsets.UTF_8)
        writeRecordHeader(context.recordContext.copy(bodyLength = (bodyBytes.size + header.identifierByteSize).toLong()))
        sink.writeId(context.id, header)
        sink.write(bodyBytes)
    }

    override fun visitLoadClassRecord(context: LoadClassContext) {
        val header = context.recordContext.header
        writeRecordHeader(context.recordContext.copy(bodyLength = (4 + header.identifierByteSize + 4 + header.identifierByteSize).toLong()))
        sink.writeUnsignedInt(context.classSerialNumber)
        sink.writeId(context.id, header)
        sink.writeUnsignedInt(context.stackTraceSerialNumber)
        sink.writeId(context.classNameStringId, header)
    }

    override fun visitUnloadClassRecord(context: UnloadClassContext) {
        writeRecordHeader(context.recordContext.copy(bodyLength = 4))
        sink.writeUnsignedInt(context.classSerialNumber)
    }

    override fun visitStackFrameRecord(context: StackFrameContext) {
        val header = context.recordContext.header
        writeRecordHeader(context.recordContext.copy(bodyLength = (header.identifierByteSize * 4 + 4 * 2).toLong()))
        sink.writeId(context.id, header)
        sink.writeId(context.methodNameStringId, header)
        sink.writeId(context.methodSignatureStringId, header)
        sink.writeId(context.sourceFileNameStringId, header)
        sink.writeUnsignedInt(context.classSerialNumber)
        sink.writeUnsignedInt(context.lineNumber)
    }

    override fun visitStackTraceRecord(context: StackTraceContext) {
        val header = context.recordContext.header
        writeRecordHeader(context.recordContext.copy(bodyLength = (context.stackFrameIds.size * header.identifierByteSize + 4 * 3).toLong()))
        sink.writeUnsignedInt(context.stackTraceSerialNumber)
        sink.writeUnsignedInt(context.threadSerialNumber)
        sink.writeUnsignedInt(context.stackFrameIds.size.toLong())
        for (id in context.stackFrameIds) {
            sink.writeId(id, header)
        }
    }

    override fun visitUnknownRecord(context: UnknownContext) {
        writeRecordHeader(context.recordContext.copy(bodyLength = context.body.size.toLong()))
        sink.write(context.body)
    }

    override fun visitHeapDumpRecorder(recordContext: RecordContext): HeapDumpRecordVisitor? {
        return HeapDumpRecordWriter(recordContext, sink)
    }


    override fun visitEnd() {
        sink.writeUnsignedByte(RecordType.HEAP_DUMP_END.tag)
        sink.writeUnsignedInt(0L)
        sink.writeUnsignedInt(0L)
        sink.flush()
    }

    private fun writeRecordHeader(context: RecordContext) {
        sink.writeUnsignedByte(context.tag)
        sink.writeUnsignedInt(context.timestamp)
        sink.writeUnsignedInt(context.bodyLength)
    }
}