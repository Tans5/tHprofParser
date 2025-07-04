package com.tans.thprofparser

import com.tans.thprofparser.records.HeapDumpRecordVisitor
import com.tans.thprofparser.records.HeapDumpRecordWriter
import com.tans.thprofparser.records.Record
import com.tans.thprofparser.records.RecordContext
import com.tans.thprofparser.records.RecordType
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.OutputStream

class HprofWriter(outputStream: OutputStream) : HprofVisitor() {

    private val sink = outputStream.sink().buffer()

    override fun visitHeader(header: HprofHeader) {
        sink.writeHprofHeader(header)
    }

    override fun visitStringRecord(context: RecordContext<Record.StringRecord>) {
        sink.writeStringRecord(context.record, context.header)
    }

    override fun visitLoadClassRecord(context: RecordContext<Record.LoadClassRecord>) {
        sink.writeLoadClassRecord(context.record, context.header)
    }

    override fun visitUnloadClassRecord(context: RecordContext<Record.UnloadClassRecord>) {
        sink.writeUnloadClassRecord(context.record, context.header)
    }

    override fun visitStackFrameRecord(context: RecordContext<Record.StackFrameRecord>) {
        sink.writeStackFrameClassRecord(context.record, context.header)
    }

    override fun visitStackTraceRecord(context: RecordContext<Record.StackTraceRecord>) {
        sink.writeStackTraceRecord(context.record, context.header)
    }

    override fun visitUnknownRecord(context: RecordContext<Record.UnknownRecord>) {
        sink.writeUnknownRecord(context.record, context.header)
    }

    override fun visitHeapDumpRecord(
        tag: Int,
        timestamp: Long,
        header: HprofHeader
    ): HeapDumpRecordVisitor? {
        return HeapDumpRecordWriter(tag, timestamp, header, sink)
    }


    override fun visitEnd() {
        sink.writeEndRecord()
    }

    companion object {

        fun BufferedSink.writeHprofHeader(header: HprofHeader) {
            writeString(header.version.versionString, Charsets.UTF_8)
            writeByte(0)
            writeInt(header.identifierByteSize)
            writeLong(header.heapDumpTimestamp)
        }

        fun BufferedSink.writeEndRecord() {
            writeUnsignedByte(RecordType.HEAP_DUMP_END.tag)
            writeUnsignedInt(0L)
            writeUnsignedInt(0L)
            flush()
        }

        fun BufferedSink.writeStringRecord(record: Record.StringRecord, header: HprofHeader) {
            val bodyBytes = record.string.toByteArray(Charsets.UTF_8)
            writeRecordHeader(record, header)
            writeId(record.id, header)
            write(bodyBytes)
        }

        fun BufferedSink.writeLoadClassRecord(record: Record.LoadClassRecord, header: HprofHeader) {
            writeRecordHeader(record, header)
            record.apply {
                writeUnsignedInt(classSerialNumber)
                writeId(id, header)
                writeUnsignedInt(stackTraceSerialNumber)
                writeId(classNameStringId, header)
            }
        }

        fun BufferedSink.writeUnloadClassRecord(record: Record.UnloadClassRecord, header: HprofHeader) {
            writeRecordHeader(record, header)
            record.apply {
                writeUnsignedInt(classSerialNumber)
            }
        }

        fun BufferedSink.writeStackFrameClassRecord(record: Record.StackFrameRecord, header: HprofHeader) {
            writeRecordHeader(record, header)
            record.apply {
                writeId(id, header)
                writeId(methodNameStringId, header)
                writeId(methodSignatureStringId, header)
                writeId(sourceFileNameStringId, header)
                writeUnsignedInt(classSerialNumber)
                writeUnsignedInt(lineNumber)
            }
        }

        fun BufferedSink.writeStackTraceRecord(record: Record.StackTraceRecord, header: HprofHeader) {
            writeRecordHeader(record, header)
            record.apply {
                writeUnsignedInt(stackTraceSerialNumber)
                writeUnsignedInt(threadSerialNumber)
                writeUnsignedInt(stackFrameIds.size.toLong())
                for (id in stackFrameIds) {
                    writeId(id, header)
                }
            }
        }

        fun BufferedSink.writeUnknownRecord(record: Record.UnknownRecord, header: HprofHeader) {
            writeRecordHeader(record, header)
            record.apply {
                write(contentBytes)
            }
        }

        fun BufferedSink.writeRecordHeader(record: Record, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(tag)
                writeUnsignedInt(timestamp)
                writeUnsignedInt(calculateBodyLen(header))
            }
        }
    }
}