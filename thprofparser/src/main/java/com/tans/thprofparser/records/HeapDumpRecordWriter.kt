package com.tans.thprofparser.records

import com.tans.thprofparser.HprofHeader
import com.tans.thprofparser.records.subrecorders.SubRecord
import com.tans.thprofparser.records.subrecorders.SubRecordContext
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
    tag: Int,
    timestamp: Long,
    header: HprofHeader,
    private val sink: BufferedSink
) : HeapDumpRecordVisitor(tag = tag, timestamp = timestamp, header = header) {

    private val subOutputStream = ByteArrayOutputStream()
    private val subSink: BufferedSink = subOutputStream.sink().buffer()

    override fun visitRootUnknownSubRecord(context: SubRecordContext<SubRecord.RootUnknownSubRecord>) {
        subSink.writeRootUnknownSubRecord(context.subRecord, header)
    }

    override fun visitRootJniGlobalSubRecord(context: SubRecordContext<SubRecord.RootJniGlobalSubRecord>) {
        subSink.writeRootJniGlobalSubRecord(context.subRecord, header)
    }

    override fun visitRootJniLocalSubRecord(context: SubRecordContext<SubRecord.RootJniLocalSubRecord>) {
        subSink.writeRootJniLocalSubRecord(context.subRecord, header)
    }

    override fun visitRootJavaFrameSubRecord(context: SubRecordContext<SubRecord.RootJavaFrameSubRecord>) {
        subSink.writeRootJavaFrameSubRecord(context.subRecord, header)
    }

    override fun visitRootNativeStackSubRecord(context: SubRecordContext<SubRecord.RootNativeStackSubRecord>) {
        subSink.writeRootNativeStackSubRecord(context.subRecord, header)
    }

    override fun visitRootStickyClassSubRecord(context: SubRecordContext<SubRecord.RootStickyClassSubRecord>) {
        subSink.writeRootStickyClassSubRecord(context.subRecord, header)
    }

    override fun visitRootThreadBlockSubRecord(context: SubRecordContext<SubRecord.RootThreadBlockSubRecord>) {
        subSink.writeRootThreadBlockSubRecord(context.subRecord, header)
    }

    override fun visitRootMonitorUsedSubRecord(context: SubRecordContext<SubRecord.RootMonitorUsedSubRecord>) {
        subSink.writeRootMonitorUsedSubRecord(context.subRecord, header)
    }

    override fun visitRootThreadObjectSubRecord(context: SubRecordContext<SubRecord.RootThreadObjectSubRecord>) {
        subSink.writeRootThreadObjectSubRecord(context.subRecord, header)
    }

    override fun visitRootInternedStringSubRecord(context: SubRecordContext<SubRecord.RootInternedStringSubRecord>) {
        subSink.writeRootInternedStringSubRecord(context.subRecord, header)
    }

    override fun visitRootFinalizingSubRecord(context: SubRecordContext<SubRecord.RootFinalizingSubRecord>) {
        subSink.writeRootFinalizingSubRecord(context.subRecord, header)
    }

    override fun visitRootDebuggerSubRecord(context: SubRecordContext<SubRecord.RootDebuggerSubRecord>) {
        subSink.writeRootDebuggerSubRecord(context.subRecord, header)
    }

    override fun visitRootReferenceCleanupSubRecord(context: SubRecordContext<SubRecord.RootReferenceCleanupSubRecord>) {
        subSink.writeRootReferenceCleanupSubRecord(context.subRecord, header)
    }

    override fun visitRootVmInternalSubRecord(context: SubRecordContext<SubRecord.RootVmInternalSubRecord>) {
        subSink.writeRootVmInternalSubRecord(context.subRecord, header)
    }

    override fun visitRootJniMonitorSubRecord(context: SubRecordContext<SubRecord.RootJniMonitorSubRecord>) {
        subSink.writeRootJniMonitorSubRecord(context.subRecord, header)
    }

    override fun visitRootUnreachableSubRecord(context: SubRecordContext<SubRecord.RootUnreachableSubRecord>) {
        subSink.writeRootUnreachableSubRecord(context.subRecord, header)
    }

    override fun visitHeapDumpInfoSubRecord(context: SubRecordContext<SubRecord.HeapDumpInfoSubRecord>) {
        subSink.writeHeapDumpInfoSubRecord(context.subRecord, header)
    }

    override fun visitClassDumpSubRecord(context: SubRecordContext<SubRecord.ClassDumpSubRecord>) {
        subSink.writeClassDumpSubRecord(context.subRecord, header)
    }

    override fun visitInstanceDumpSubRecord(context: SubRecordContext<SubRecord.InstanceDumpSubRecord>) {
        subSink.writeInstanceDumpSubRecord(context.subRecord, header)
    }

    override fun visitObjectArrayDumpSubRecord(context: SubRecordContext<SubRecord.ObjectArrayDumpSubRecord>) {
        subSink.writeObjectArrayDumpSubRecord(context.subRecord, header)
    }

    override fun visitPrimitiveArrayDumpSubRecord(context: SubRecordContext<SubRecord.PrimitiveArrayDumpSubRecord>) {
        subSink.writePrimitiveArrayDumpSubRecord(context.subRecord, header)
    }

    override fun visitEnd() {
        subSink.flush()
        val bodyBytes = subOutputStream.toByteArray()
        sink.apply {
            writeUnsignedByte(tag)
            writeUnsignedInt(timestamp)
            writeUnsignedInt(bodyBytes.size.toLong())
            write(bodyBytes)
        }
    }

    fun flush() {
        subSink.flush()
    }

    companion object {

        fun BufferedSink.writeRootUnknownSubRecord(record: SubRecord.RootUnknownSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(id, header)
            }
        }


        fun BufferedSink.writeRootJniGlobalSubRecord(record: SubRecord.RootJniGlobalSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(id, header)
                writeId(refId, header)
            }
        }


        fun BufferedSink.writeRootJniLocalSubRecord(record: SubRecord.RootJniLocalSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(id, header)
                writeUnsignedInt(threadSerialNumber)
                writeUnsignedInt(frameNumber)
            }
        }



        fun BufferedSink.writeRootJavaFrameSubRecord(record: SubRecord.RootJavaFrameSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(id, header)
                writeUnsignedInt(threadSerialNumber)
                writeUnsignedInt(frameNumber)
            }
        }


        fun BufferedSink.writeRootNativeStackSubRecord(record: SubRecord.RootNativeStackSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(id, header)
                writeUnsignedInt(threadSerialNumber)
            }
        }


        fun BufferedSink.writeRootStickyClassSubRecord(record: SubRecord.RootStickyClassSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(id, header)
            }
        }


        fun BufferedSink.writeRootThreadBlockSubRecord(record: SubRecord.RootThreadBlockSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(id, header)
                writeUnsignedInt(threadSerialNumber)
            }
        }

        fun BufferedSink.writeRootMonitorUsedSubRecord(record: SubRecord.RootMonitorUsedSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(id, header)
            }
        }


        fun BufferedSink.writeRootThreadObjectSubRecord(record: SubRecord.RootThreadObjectSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(id, header)
                writeUnsignedInt(threadSerialNumber)
                writeUnsignedInt(frameNumber)
            }
        }


        fun BufferedSink.writeRootInternedStringSubRecord(record: SubRecord.RootInternedStringSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(id, header)
            }
        }


        fun BufferedSink.writeRootFinalizingSubRecord(record: SubRecord.RootFinalizingSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(id, header)
            }
        }

        fun BufferedSink.writeRootDebuggerSubRecord(record: SubRecord.RootDebuggerSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(id, header)
            }
        }


        fun BufferedSink.writeRootReferenceCleanupSubRecord(record: SubRecord.RootReferenceCleanupSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(id, header)
            }
        }

        fun BufferedSink.writeRootVmInternalSubRecord(record: SubRecord.RootVmInternalSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(id, header)
            }
        }

        fun BufferedSink.writeRootJniMonitorSubRecord(record: SubRecord.RootJniMonitorSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(id, header)
                writeUnsignedInt(threadSerialNumber)
                writeUnsignedInt(stackDepth)
            }
        }

        fun BufferedSink.writeRootUnreachableSubRecord(record: SubRecord.RootUnreachableSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(id, header)
            }
        }

        fun BufferedSink.writeHeapDumpInfoSubRecord(record: SubRecord.HeapDumpInfoSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(heapId, header)
                writeId(stringId, header)
            }
        }

        fun BufferedSink.writeClassDumpSubRecord(record: SubRecord.ClassDumpSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(id, header)
                writeUnsignedInt(stackTraceSerialNumber)
                writeId(superClassId, header)
                writeId(classLoaderId, header)
                writeId(signerId, header)
                writeId(protectionDomainId, header)
                writeId(unknownId1, header)
                writeId(unknownId2, header)
                writeUnsignedInt(instanceSize)
                writeUnsignedShort(constFields.size)
                for (constField in constFields) {
                    writeConstField(constField, header)
                }
                writeUnsignedShort(staticFields.size)
                for (staticField in staticFields) {
                    writeStaticField(staticField, header)
                }
                writeUnsignedShort(memberFields.size)
                for (memberField in memberFields) {
                    writeMemberField(memberField, header)
                }
            }
        }

        fun BufferedSink.writeInstanceDumpSubRecord(record: SubRecord.InstanceDumpSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(id, header)
                writeUnsignedInt(stackTraceSerialNumber)
                writeId(classId, header)
                writeUnsignedInt(contentBytes.size.toLong())
                write(contentBytes)
            }
        }

        fun BufferedSink.writeObjectArrayDumpSubRecord(record: SubRecord.ObjectArrayDumpSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(id, header)
                writeUnsignedInt(stackTraceSerialNumber)
                writeUnsignedInt(elementIds.size.toLong())
                writeId(arrayClassId, header)
                for (elementId in elementIds) {
                    writeId(elementId, header)
                }
            }
        }

        fun BufferedSink.writePrimitiveArrayDumpSubRecord(record: SubRecord.PrimitiveArrayDumpSubRecord, header: HprofHeader) {
            record.apply {
                writeUnsignedByte(subTag)
                writeId(id, header)
                writeUnsignedInt(stackTraceSerialNumber)
                writeUnsignedInt(elements.size.toLong())
                writeUnsignedByte(type)
                for (element in elements) {
                    writeValue(element, header, false)
                }
            }
        }
    }
}