package com.tans.thprofparser

import com.tans.thprofparser.records.Record
import com.tans.thprofparser.records.RecordContext
import com.tans.thprofparser.records.RecordType
import com.tans.thprofparser.records.subrecorders.SubRecord
import com.tans.thprofparser.records.subrecorders.SubRecordContext
import com.tans.thprofparser.records.subrecorders.SubRecordType
import okio.BufferedSource
import okio.buffer
import okio.source
import java.io.ByteArrayInputStream
import java.io.InputStream

class HprofReader(inputStream: InputStream) {

    private val source: BufferedSource = inputStream.source().buffer()
    private val header: HprofHeader = source.readHprofHeader()

    fun accept(
        hprofVisitor: HprofVisitor
    ) {
        var offset = header.len

        hprofVisitor.visitHeader(header)

        while (!source.exhausted()) {
            val tag = source.readUnsignedByte()
            when (tag) {
                RecordType.STRING_IN_UTF8.tag -> {
                    val record = source.readStringRecord(false, header)
                    hprofVisitor.visitStringRecord(RecordContext(header, offset, record))
                    offset += record.bodyLen
                }
                RecordType.LOAD_CLASS.tag -> {
                    val record = source.readLoadClassRecord(false, header)
                    hprofVisitor.visitLoadClassRecord(RecordContext(header, offset, record))
                    offset += record.bodyLen
                }
                RecordType.UNLOAD_CLASS.tag -> {
                    val record = source.readUnloadClassRecord(false )
                    hprofVisitor.visitUnloadClassRecord(RecordContext(header, offset, record))
                    offset += record.bodyLen
                }
                RecordType.STACK_FRAME.tag -> {
                    val record = source.readStackFrameRecord(false, header)
                    hprofVisitor.visitStackFrameRecord(RecordContext(header, offset, record))
                    offset += record.bodyLen
                }
                RecordType.STACK_TRACE.tag -> {
                    val record = source.readStackTraceRecord(false, header)
                    hprofVisitor.visitStackTraceRecord(RecordContext(header, offset, record))
                    offset += record.bodyLen
                }
                RecordType.HEAP_DUMP.tag -> {
                    val record = readHeapDumpRecord(hprofVisitor, tag, offset)
                    offset += record.bodyLen
                }
                RecordType.HEAP_DUMP_SEGMENT.tag -> {
                    val record = readHeapDumpRecord(hprofVisitor, tag, offset)
                    offset += record.bodyLen
                }
                RecordType.HEAP_DUMP_END.tag -> {
                    break
                }
                else -> {
                    val record = source.readUnknownRecord(tag)
                    hprofVisitor.visitUnknownRecord(RecordContext(header, offset, record))
                    offset += record.bodyLen
                }
            }
            offset += 9
        }
        hprofVisitor.visitEnd()
    }

    private fun readHeapDumpRecord(
        hprofVisitor: HprofVisitor,
        tag: Int,
        offset: Long
    ): Record.HeapDumpRecord {
        val (_, timestamp, bodyLen) = source.readRecordCommonHeader(false)
        val heapDumpVisitor = hprofVisitor.visitHeapDumpRecord(tag, timestamp, header)
        val contentBytes = source.readByteArray(bodyLen)
        var subRecordOffset: Long = 9
        val subRecords = ArrayList<SubRecord>()
        ByteArrayInputStream(contentBytes).source().buffer().use { subSource ->
            while (!subSource.exhausted()) {
                val subTag = subSource.readUnsignedByte()
                when (subTag) {
                    SubRecordType.ROOT_UNKNOWN.tag -> {
                        val record = subSource.readRootUnknownSubRecord(false, header)
                        heapDumpVisitor?.visitRootUnknownSubRecord(SubRecordContext(header, tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)

                    }
                    SubRecordType.ROOT_JNI_GLOBAL.tag -> {
                        val record = subSource.readRootJniGlobalSubRecord(false, header)
                        heapDumpVisitor?.visitRootJniGlobalSubRecord(SubRecordContext(header,tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)
                    }
                    SubRecordType.ROOT_JNI_LOCAL.tag -> {
                        val record = subSource.readRootJniLocalSubRecord(false, header)
                        heapDumpVisitor?.visitRootJniLocalSubRecord(SubRecordContext(header,tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)
                    }
                    SubRecordType.ROOT_JAVA_FRAME.tag -> {
                        val record = subSource.readRootJavaFrameSubRecord(false, header)
                        heapDumpVisitor?.visitRootJavaFrameSubRecord(SubRecordContext(header,tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)
                    }
                    SubRecordType.ROOT_NATIVE_STACK.tag -> {
                        val record = subSource.readRootNativeStackSubRecord(false, header)
                        heapDumpVisitor?.visitRootNativeStackSubRecord(SubRecordContext(header,tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)
                    }
                    SubRecordType.ROOT_STICKY_CLASS.tag -> {
                        val record = subSource.readRootStickyClassSubRecord(false, header)
                        heapDumpVisitor?.visitRootStickyClassSubRecord(SubRecordContext(header,tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)
                    }
                    SubRecordType.ROOT_THREAD_BLOCK.tag -> {
                        val record = subSource.readRootThreadBlockSubRecord(false, header)
                        heapDumpVisitor?.visitRootThreadBlockSubRecord(SubRecordContext(header,tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)
                    }
                    SubRecordType.ROOT_MONITOR_USED.tag -> {
                        val record = subSource.readRootMonitorUsedRecord(false, header)
                        heapDumpVisitor?.visitRootMonitorUsedSubRecord(SubRecordContext(header,tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)
                    }
                    SubRecordType.ROOT_THREAD_OBJECT.tag -> {
                        val record = subSource.readRootThreadObjectSubRecord(false, header)
                        heapDumpVisitor?.visitRootThreadObjectSubRecord(SubRecordContext(header,tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)
                    }
                    SubRecordType.ROOT_INTERNED_STRING.tag -> {
                        val record = subSource.readRootInternedStringRecord(false, header)
                        heapDumpVisitor?.visitRootInternedStringSubRecord(SubRecordContext(header,tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)
                    }
                    SubRecordType.ROOT_FINALIZING.tag -> {
                        val record = subSource.readRootFinalizingSubRecord(false, header)
                        heapDumpVisitor?.visitRootFinalizingSubRecord(SubRecordContext(header,tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)
                    }
                    SubRecordType.ROOT_DEBUGGER.tag -> {
                        val record = subSource.readRootDebuggerSubRecord(false, header)
                        heapDumpVisitor?.visitRootDebuggerSubRecord(SubRecordContext(header,tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)
                    }
                    SubRecordType.ROOT_REFERENCE_CLEANUP.tag -> {
                        val record = subSource.readRootReferenceCleanSubRecord(false, header)
                        heapDumpVisitor?.visitRootReferenceCleanupSubRecord(SubRecordContext(header,tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)
                    }
                    SubRecordType.ROOT_VM_INTERNAL.tag -> {
                        val record = subSource.readRootVmInternalSubRecord(false, header)
                        heapDumpVisitor?.visitRootVmInternalSubRecord(SubRecordContext(header,tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)
                    }
                    SubRecordType.ROOT_JNI_MONITOR.tag -> {
                        val record = subSource.readRootJniMonitorSubRecord(false, header)
                        heapDumpVisitor?.visitRootJniMonitorSubRecord(SubRecordContext(header,tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)
                    }
                    SubRecordType.ROOT_UNREACHABLE.tag -> {
                        val record = subSource.readRootUnreachableSubRecord(false, header)
                        heapDumpVisitor?.visitRootUnreachableSubRecord(SubRecordContext(header,tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)
                    }

                    SubRecordType.CLASS_DUMP.tag -> {
                        val record = subSource.readClassDumpSubRecord(false, header)
                        heapDumpVisitor?.visitClassDumpSubRecord(SubRecordContext(header,tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)
                    }

                    SubRecordType.INSTANCE_DUMP.tag -> {
                        val record = subSource.readInstanceDumpSubRecord(false, header)
                        heapDumpVisitor?.visitInstanceDumpSubRecord(SubRecordContext(header,tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)
                    }

                    SubRecordType.OBJECT_ARRAY_DUMP.tag -> {
                        val record = subSource.readObjectArrayDumpSubRecord(false, header)
                        heapDumpVisitor?.visitObjectArrayDumpSubRecord(SubRecordContext(header,tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)
                    }

                    SubRecordType.PRIMITIVE_ARRAY_DUMP.tag -> {
                        val record = subSource.readPrimitiveArrayDumpSubRecord(false, header)
                        heapDumpVisitor?.visitPrimitiveArrayDumpSubRecord(SubRecordContext(header,tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)
                    }


                    SubRecordType.HEAP_DUMP_INFO.tag -> {
                        val record = subSource.readHeapInfoSubRecord(false, header)
                        heapDumpVisitor?.visitHeapDumpInfoSubRecord(SubRecordContext(header,tag, offset, offset + subRecordOffset, bodyLen, record))
                        subRecords.add(record)
                        subRecordOffset += record.calculateBodyLen(header)
                    }

                    else -> {
                        break
                    }
                }

                subRecordOffset += 1
            }
        }
        heapDumpVisitor?.visitEnd()

        return Record.HeapDumpRecord(
            tag = tag,
            timestamp = timestamp,
            bodyLen = bodyLen,
            subRecords = subRecords
        )
    }

    companion object {

        // region Record
        fun BufferedSource.readHprofHeader(): HprofHeader {
            val endOfVersionStrIndex = indexOf(0x00)
            val versionStr = readUtf8(endOfVersionStrIndex)
            val version = HprofVersion.entries.find { it.versionString == versionStr }
            if (version == null) {
                throw HprofParserException("Unknown hprof version: $versionStr")
            }
            skip(1)
            val identifierBytesSize = readInt()
            val heapDumpTimestamp = readLong()
            return HprofHeader(
                version = version,
                identifierByteSize = identifierBytesSize,
                heapDumpTimestamp = heapDumpTimestamp,
                len = endOfVersionStrIndex + 1 + 4 + 8
            )
        }

        fun BufferedSource.readStringRecord(readTag: Boolean, header: HprofHeader): Record.StringRecord {
            val (_, timestamp, bodyLen) = readRecordCommonHeader(readTag)
            val id = readId(header)
            val str = readString(bodyLen - header.identifierByteSize, Charsets.UTF_8)
            return Record.StringRecord(
                timestamp = timestamp,
                bodyLen = bodyLen,
                id = id,
                string = str
            )
        }

        fun BufferedSource.readLoadClassRecord(readTag: Boolean, header: HprofHeader): Record.LoadClassRecord {
            val (_, timestamp, bodyLen) = readRecordCommonHeader(readTag)
            val classSerialNumber = readUnsignedInt()
            val id = readId(header)
            val stackTraceSerialNumber = readUnsignedInt()
            val classNameStringId = readId(header)
            return Record.LoadClassRecord(
                timestamp = timestamp,
                bodyLen = bodyLen,
                classSerialNumber = classSerialNumber,
                id = id,
                stackTraceSerialNumber = stackTraceSerialNumber,
                classNameStringId = classNameStringId
            )
        }

        fun BufferedSource.readUnloadClassRecord(readTag: Boolean): Record.UnloadClassRecord {
            val (_, timestamp, bodyLen) = readRecordCommonHeader(readTag)
            val classSerialNumber = readUnsignedInt()
            return Record.UnloadClassRecord(
                timestamp = timestamp,
                bodyLen = bodyLen,
                classSerialNumber = classSerialNumber
            )
        }

        fun BufferedSource.readStackFrameRecord(readTag: Boolean, header: HprofHeader): Record.StackFrameRecord {
            val (_, timestamp, bodyLen) = readRecordCommonHeader(readTag)
            val id = readId(header)
            val methodNameStringId = readId(header)
            val methodSignatureStringId = readId(header)
            val sourceFileNameStringId = readId(header)
            val classSerialNumber = readUnsignedInt()
            val lineNumber = readUnsignedInt()
            return Record.StackFrameRecord(
                timestamp = timestamp,
                bodyLen = bodyLen,
                id = id,
                methodNameStringId = methodNameStringId,
                methodSignatureStringId = methodSignatureStringId,
                sourceFileNameStringId = sourceFileNameStringId,
                classSerialNumber = classSerialNumber,
                lineNumber = lineNumber
            )
        }

        fun BufferedSource.readStackTraceRecord(readTag: Boolean, header: HprofHeader): Record.StackTraceRecord {
            val (_, timestamp, bodyLen) = readRecordCommonHeader(readTag)
            val stackTraceSerialNumber = readUnsignedInt()
            val threadSerialNumber = readUnsignedInt()
            val stackFrameSize = readUnsignedInt()
            val stackFrameIds = ArrayList<Long>()
            repeat(stackFrameSize.toInt()) {
                stackFrameIds.add(readId(header))
            }
            return Record.StackTraceRecord(
                timestamp = timestamp,
                bodyLen = bodyLen,
                stackTraceSerialNumber = stackTraceSerialNumber,
                threadSerialNumber = threadSerialNumber,
                stackFrameIds = stackFrameIds
            )
        }

        fun BufferedSource.readUnknownRecord(tag: Int?): Record.UnknownRecord {
            val (t, timestamp, bodyLen) = readRecordCommonHeader(tag == null)
            val contentBytes = readByteArray(bodyLen)
            return Record.UnknownRecord(
                timestamp = timestamp,
                bodyLen = bodyLen,
                tag = tag ?: t,
                contentBytes = contentBytes
            )
        }

        fun BufferedSource.readRecordCommonHeader(readTag: Boolean): Triple<Int, Long, Long> {
            var tag: Int = -1
            if (readTag) {
                tag = readUnsignedByte()
            }
            return Triple(tag, readUnsignedInt(), readUnsignedInt())
        }
        // endregion

        // region SubRecord
        fun BufferedSource.readRootUnknownSubRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.RootUnknownSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val id = readId(header)
            return SubRecord.RootUnknownSubRecord(id = id)
        }

        fun BufferedSource.readRootJniGlobalSubRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.RootJniGlobalSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val id = readId(header)
            val refId = readId(header)
            return SubRecord.RootJniGlobalSubRecord(id = id, refId = refId)
        }

        fun BufferedSource.readRootJniLocalSubRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.RootJniLocalSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val id = readId(header)
            val threadSerialNumber = readUnsignedInt()
            val frameNumber = readUnsignedInt()
            return SubRecord.RootJniLocalSubRecord(
                id = id,
                threadSerialNumber = threadSerialNumber,
                frameNumber = frameNumber
            )
        }

        fun BufferedSource.readRootJavaFrameSubRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.RootJavaFrameSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val id = readId(header)
            val threadSerialNumber = readUnsignedInt()
            val frameNumber = readUnsignedInt()
            return SubRecord.RootJavaFrameSubRecord(
                id = id,
                threadSerialNumber = threadSerialNumber,
                frameNumber = frameNumber
            )
        }

        fun BufferedSource.readRootNativeStackSubRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.RootNativeStackSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val id = readId(header)
            val threadSerialNumber = readUnsignedInt()
            return SubRecord.RootNativeStackSubRecord(
                id = id,
                threadSerialNumber = threadSerialNumber
            )
        }

        fun BufferedSource.readRootStickyClassSubRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.RootStickyClassSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val id = readId(header)
            return SubRecord.RootStickyClassSubRecord(id = id)
        }

        fun BufferedSource.readRootThreadBlockSubRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.RootThreadBlockSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val id = readId(header)
            val threadSerialNumber = readUnsignedInt()
            return SubRecord.RootThreadBlockSubRecord(
                id = id,
                threadSerialNumber = threadSerialNumber
            )
        }

        fun BufferedSource.readRootMonitorUsedRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.RootMonitorUsedSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val id = readId(header)
            return SubRecord.RootMonitorUsedSubRecord(id = id)
        }

        fun BufferedSource.readRootThreadObjectSubRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.RootThreadObjectSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val id = readId(header)
            val threadSerialNumber = readUnsignedInt()
            val frameNumber = readUnsignedInt()
            return SubRecord.RootThreadObjectSubRecord(
                id = id,
                threadSerialNumber = threadSerialNumber,
                frameNumber = frameNumber
            )
        }

        fun BufferedSource.readRootInternedStringRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.RootInternedStringSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val id = readId(header)
            return SubRecord.RootInternedStringSubRecord(id = id)
        }

        fun BufferedSource.readRootFinalizingSubRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.RootFinalizingSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val id = readId(header)
            return SubRecord.RootFinalizingSubRecord(id = id)
        }

        fun BufferedSource.readRootDebuggerSubRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.RootDebuggerSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val id = readId(header)
            return SubRecord.RootDebuggerSubRecord(id = id)
        }

        fun BufferedSource.readRootReferenceCleanSubRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.RootReferenceCleanupSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val id = readId(header)
            return SubRecord.RootReferenceCleanupSubRecord(id = id)
        }

        fun BufferedSource.readRootVmInternalSubRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.RootVmInternalSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val id = readId(header)
            return SubRecord.RootVmInternalSubRecord(id = id)
        }

        fun BufferedSource.readRootJniMonitorSubRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.RootJniMonitorSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val id = readId(header)
            val threadSerialNumber = readUnsignedInt()
            val stackDepth = readUnsignedInt()
            return SubRecord.RootJniMonitorSubRecord(
                id = id,
                threadSerialNumber = threadSerialNumber,
                stackDepth = stackDepth
            )
        }

        fun BufferedSource.readRootUnreachableSubRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.RootUnreachableSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val id = readId(header)
            return SubRecord.RootUnreachableSubRecord(id = id)
        }

        fun BufferedSource.readClassDumpSubRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.ClassDumpSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val id = readId(header)
            val stackTraceSerialNumber = readUnsignedInt()
            val superClassId = readId(header)
            val classLoaderId = readId(header)
            val signerId = readId(header)
            val protectionDomainId = readId(header)
            val unknownId1 = readId(header)
            val unknownId2 = readId(header)
            val instanceSize = readUnsignedInt()
            val constFieldSize = readUnsignedShort()
            val constFields = ArrayList<ConstField>()
            repeat(constFieldSize) {
                constFields.add(readConstField(header))
            }
            val staticFieldSize = readUnsignedShort()
            val staticFields = ArrayList<StaticField>()
            repeat(staticFieldSize) {
                staticFields.add(readStaticField(header))
            }
            val memberFieldSize = readUnsignedShort()
            val memberFields = ArrayList<MemberField>()
            repeat(memberFieldSize) {
                memberFields.add(readMemberField(header))
            }
            return SubRecord.ClassDumpSubRecord(
                id = id,
                stackTraceSerialNumber = stackTraceSerialNumber,
                superClassId = superClassId,
                classLoaderId = classLoaderId,
                signerId = signerId,
                protectionDomainId = protectionDomainId,
                unknownId1 = unknownId1,
                unknownId2 = unknownId2,
                instanceSize = instanceSize,
                constFields = constFields,
                staticFields = staticFields,
                memberFields = memberFields
            )
        }

        fun BufferedSource.readInstanceDumpSubRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.InstanceDumpSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val id = readId(header)
            val stackTraceSerialNumber = readUnsignedInt()
            val classId = readId(header)
            val contentSize = readUnsignedInt()
            val contentBytes = readByteArray(contentSize)
            return SubRecord.InstanceDumpSubRecord(
                id = id,
                stackTraceSerialNumber = stackTraceSerialNumber,
                classId = classId,
                contentBytes = contentBytes
            )
        }

        fun BufferedSource.readObjectArrayDumpSubRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.ObjectArrayDumpSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val id = readId(header)
            val stackTraceSerialNumber = readUnsignedInt()
            val arrayLength = readUnsignedInt()
            val arrayClassId = readId(header)
            val elementIds = ArrayList<Long>()
            repeat(arrayLength.toInt()) {
                elementIds.add(readId(header))
            }
            return SubRecord.ObjectArrayDumpSubRecord(
                id = id,
                stackTraceSerialNumber = stackTraceSerialNumber,
                arrayClassId = arrayClassId,
                elementIds = elementIds
            )
        }

        fun BufferedSource.readPrimitiveArrayDumpSubRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.PrimitiveArrayDumpSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val id = readId(header)
            val stackTraceSerialNumber = readUnsignedInt()
            val arrayLength = readUnsignedInt()
            val type = readUnsignedByte()
            val elements = ArrayList<ValueHolder>()
            repeat(arrayLength.toInt()) {
                elements.add(readValue(type, header))
            }
            return SubRecord.PrimitiveArrayDumpSubRecord(
                id = id,
                stackTraceSerialNumber = stackTraceSerialNumber,
                type = type,
                elements = elements
            )
        }

        fun BufferedSource.readHeapInfoSubRecord(
            readSubTag: Boolean,
            header: HprofHeader
        ): SubRecord.HeapDumpInfoSubRecord {
            if (readSubTag) {
                skip(1)
            }
            val heapId = readId(header)
            val stringId = readId(header)
            return SubRecord.HeapDumpInfoSubRecord(heapId = heapId, stringId = stringId)
        }
        // endregion
    }

}