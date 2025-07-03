package com.tans.thprofparser.records

import com.tans.thprofparser.HprofHeader
import com.tans.thprofparser.records.subrecorders.ClassDumpContext
import com.tans.thprofparser.records.subrecorders.FrameRootContext
import com.tans.thprofparser.records.subrecorders.HeapDumpInfoContext
import com.tans.thprofparser.records.subrecorders.IdRootContext
import com.tans.thprofparser.records.subrecorders.InstanceDumpContext
import com.tans.thprofparser.records.subrecorders.ObjectArrayDumpContext
import com.tans.thprofparser.records.subrecorders.PrimitiveArrayDumpContext
import com.tans.thprofparser.records.subrecorders.RefRootContext
import com.tans.thprofparser.records.subrecorders.StackRootContext
import com.tans.thprofparser.records.subrecorders.ThreadRootContext
import com.tans.thprofparser.writeConstField
import com.tans.thprofparser.writeId
import com.tans.thprofparser.writeMemberField
import com.tans.thprofparser.writeStaticField
import com.tans.thprofparser.writeUnsignedByte
import com.tans.thprofparser.writeUnsignedInt
import com.tans.thprofparser.writeUnsignedShort
import com.tans.thprofparser.writeValue
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.ByteArrayOutputStream

class HeapDumpRecordWriter(
    recordContext: RecordContext,
    private val sink: BufferedSink
) : HeapDumpRecordVisitor(recordContext) {

    private val header: HprofHeader = recordContext.header

    private val subOutputStream = ByteArrayOutputStream()
    private val subSink: BufferedSink = subOutputStream.sink().buffer()

    override fun visitRootUnknownSubRecord(context: IdRootContext) {
        writeIdRootContext(context)
    }

    override fun visitRootJniGlobalSubRecord(context: RefRootContext) {
        subSink.apply {
            writeUnsignedByte(context.heapDumpContext.subTag)
            writeId(context.id, header)
            writeId(context.refId, header)
        }
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
        subSink.apply {
            writeUnsignedByte(context.heapDumpContext.subTag)
            writeId(context.id, header)
            writeUnsignedInt(context.threadSerialNumber)
            writeUnsignedInt(context.stackDepth)
        }
    }

    override fun visitRootUnreachableSubRecord(context: IdRootContext) {
        writeIdRootContext(context)
    }

    override fun visitHeapDumpInfoSubRecord(context: HeapDumpInfoContext) {
        subSink.apply {
           writeUnsignedByte(context.heapDumpContext.subTag)
           writeId(context.heapId, header)
           writeId(context.stringId, header)
        }
    }

    override fun visitClassDumpSubRecord(context: ClassDumpContext) {
        subSink.apply {
            writeUnsignedByte(context.heapDumpContext.subTag)
            writeId(context.id, header)
            writeUnsignedInt(context.stackTraceSerialNumber)
            writeId(context.superClassId, header)
            writeId(context.classLoaderId, header)
            writeId(context.signerId, header)
            writeId(context.protectionDomainId, header)
            writeId(context.unknownId1, header)
            writeId(context.unknownId2, header)
            writeUnsignedInt(context.instanceSize)
            writeUnsignedShort(context.constFields.size)
            for (constField in context.constFields) {
                writeConstField(constField, header)
            }
            writeUnsignedShort(context.staticFields.size)
            for (staticField in context.staticFields) {
                writeStaticField(staticField, header)
            }
            writeUnsignedShort(context.memberFields.size)
            for (memberField in context.memberFields) {
                writeMemberField(memberField, header)
            }
        }
    }

    override fun visitInstanceDumpSubRecord(context: InstanceDumpContext) {
        subSink.apply {
            writeUnsignedByte(context.heapDumpContext.subTag)
            writeId(context.id, header)
            writeUnsignedInt(context.stackTraceSerialNumber)
            writeId(context.classId, header)
            writeUnsignedInt(context.contentBytes.size.toLong())
            write(context.contentBytes)
        }
    }

    override fun visitObjectArrayDumpSubRecord(context: ObjectArrayDumpContext) {
        subSink.apply {
            writeUnsignedByte(context.heapDumpContext.subTag)
            writeId(context.id, header)
            writeUnsignedInt(context.stackTraceSerialNumber)
            writeUnsignedInt(context.elementIds.size.toLong())
            writeId(context.arrayClassId, header)
            for (elementId in context.elementIds) {
                writeId(elementId, header)
            }
        }
    }

    override fun visitPrimitiveArrayDumpSubRecord(context: PrimitiveArrayDumpContext) {
        subSink.apply {
            writeUnsignedByte(context.heapDumpContext.subTag)
            writeId(context.id, header)
            writeUnsignedInt(context.stackTraceSerialNumber)
            writeUnsignedInt(context.elements.size.toLong())
            writeUnsignedByte(context.type)
            for (element in context.elements) {
                writeValue(element, header, false)
            }
        }
    }

    override fun visitEnd() {
        subSink.flush()
        val bodyBytes = subOutputStream.toByteArray()
        sink.apply {
            writeUnsignedByte(recordContext.tag)
            writeUnsignedInt(recordContext.timestamp)
            writeUnsignedInt(bodyBytes.size.toLong())
            write(bodyBytes)
        }
    }

    private fun writeIdRootContext(context: IdRootContext) {
        subSink.apply {
            writeUnsignedByte(context.heapDumpContext.subTag)
            writeId(context.id, header)
        }
    }

    private fun writeThreadRootContext(context: ThreadRootContext) {
        subSink.apply {
           writeUnsignedByte(context.heapDumpContext.subTag)
           writeId(context.id, header)
           writeUnsignedInt(context.threadSerialNumber)
        }
    }

    private fun writeFrameRootContext(context: FrameRootContext) {
        subSink.apply {
            writeUnsignedByte(context.heapDumpContext.subTag)
            writeId(context.id, header)
            writeUnsignedInt(context.threadSerialNumber)
            writeUnsignedInt(context.frameNumber)
        }
    }
}