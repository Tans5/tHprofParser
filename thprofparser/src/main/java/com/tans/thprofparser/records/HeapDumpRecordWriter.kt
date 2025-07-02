package com.tans.thprofparser.records

import com.tans.thprofparser.records.subrecorders.FrameRootContext
import com.tans.thprofparser.records.subrecorders.HeapDumpInfoContext
import com.tans.thprofparser.records.subrecorders.IdRootContext
import com.tans.thprofparser.records.subrecorders.RefRootContext
import com.tans.thprofparser.records.subrecorders.StackRootContext
import com.tans.thprofparser.records.subrecorders.ThreadRootContext
import com.tans.thprofparser.writeId
import com.tans.thprofparser.writeUnsignedByte
import com.tans.thprofparser.writeUnsignedInt
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.ByteArrayOutputStream

class HeapDumpRecordWriter(
    recordContext: RecordContext,
    private val sink: BufferedSink
) : HeapDumpRecordVisitor(recordContext) {

    private val subOutputStream = ByteArrayOutputStream()
    private val subSink: BufferedSink = subOutputStream.sink().buffer()

    override fun visitRootUnknownSubRecord(context: IdRootContext) {
        writeIdRootContext(context)
    }

    override fun visitRootJniGlobalSubRecord(context: RefRootContext) {
        subSink.writeUnsignedByte(context.heapDumpContext.subTag)
        subSink.writeId(context.id, context.heapDumpContext.recordContext.header)
        subSink.writeId(context.refId, context.heapDumpContext.recordContext.header)
    }

    override fun visitRootJniLocalSubRecord(context: FrameRootContext) {
        writeFrameRootContext(context)
    }

    override fun visitRootJavaFrameSubRecord(context: FrameRootContext) {
        writeFrameRootContext(context)
    }

    override fun visitRootNativeStackSubRecord(context: ThreadRootContext) {
        writeThreadRootContext(context)
    }

    override fun visitRootStickyClassSubRecord(context: IdRootContext) {
        writeIdRootContext(context)
    }

    override fun visitRootThreadBlockSubRecord(context: ThreadRootContext) {
        writeThreadRootContext(context)
    }

    override fun visitRootMonitorUsedSubRecord(context: IdRootContext) {
        writeIdRootContext(context)
    }

    override fun visitRootThreadObjectSubRecord(context: FrameRootContext) {
        writeFrameRootContext(context)
    }

    override fun visitRootInternedStringSubRecord(context: IdRootContext) {
        writeIdRootContext(context)
    }

    override fun visitRootFinalizingSubRecord(context: IdRootContext) {
        writeIdRootContext(context)
    }

    override fun visitRootDebuggerSubRecord(context: IdRootContext) {
        writeIdRootContext(context)
    }

    override fun visitRootReferenceCleanupSubRecord(context: IdRootContext) {
        writeIdRootContext(context)
    }

    override fun visitRootVmInternalSubRecord(context: IdRootContext) {
        writeIdRootContext(context)
    }

    override fun visitRootJniMonitorSubRecord(context: StackRootContext) {
        subSink.writeUnsignedByte(context.heapDumpContext.subTag)
        subSink.writeId(context.id, context.heapDumpContext.recordContext.header)
        subSink.writeUnsignedInt(context.threadSerialNumber)
        subSink.writeUnsignedInt(context.stackDepth)
    }

    override fun visitRootUnreachableSubRecord(context: IdRootContext) {
        writeIdRootContext(context)
    }

    override fun visitHeapDumpInfoSubRecord(context: HeapDumpInfoContext) {
        subSink.writeUnsignedByte(context.heapDumpContext.subTag)
        subSink.writeId(context.heapId, context.heapDumpContext.recordContext.header)
        subSink.writeId(context.stringId, context.heapDumpContext.recordContext.header)
    }

    override fun visitEnd() {
        subSink.flush()
        val bodyBytes = subOutputStream.toByteArray()
        sink.writeUnsignedByte(recordContext.tag)
        sink.writeUnsignedInt(recordContext.timestamp)
        sink.writeUnsignedInt(bodyBytes.size.toLong())
        sink.write(bodyBytes)
    }

    private fun writeIdRootContext(context: IdRootContext) {
        subSink.writeUnsignedByte(context.heapDumpContext.subTag)
        subSink.writeId(context.id, context.heapDumpContext.recordContext.header)
    }

    private fun writeThreadRootContext(context: ThreadRootContext) {
        subSink.writeUnsignedByte(context.heapDumpContext.subTag)
        subSink.writeId(context.id, context.heapDumpContext.recordContext.header)
        subSink.writeUnsignedInt(context.threadSerialNumber)
    }

    private fun writeFrameRootContext(context: FrameRootContext) {
        subSink.writeUnsignedByte(context.heapDumpContext.subTag)
        subSink.writeId(context.id, context.heapDumpContext.recordContext.header)
        subSink.writeUnsignedInt(context.threadSerialNumber)
        subSink.writeUnsignedInt(context.frameNumber)
    }
}