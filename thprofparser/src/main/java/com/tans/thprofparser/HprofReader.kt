package com.tans.thprofparser

import com.tans.thprofparser.records.HeapDumpContext
import com.tans.thprofparser.records.LoadClassContext
import com.tans.thprofparser.records.RecordContext
import com.tans.thprofparser.records.RecordType
import com.tans.thprofparser.records.StackFrameContext
import com.tans.thprofparser.records.StackTraceContext
import com.tans.thprofparser.records.StringContext
import com.tans.thprofparser.records.UnknownContext
import com.tans.thprofparser.records.UnloadClassContext
import com.tans.thprofparser.records.subrecorders.ClassDumpContext
import com.tans.thprofparser.records.subrecorders.FrameRootContext
import com.tans.thprofparser.records.subrecorders.HeapDumpInfoContext
import com.tans.thprofparser.records.subrecorders.IdRootContext
import com.tans.thprofparser.records.subrecorders.InstanceDumpContext
import com.tans.thprofparser.records.subrecorders.ObjectArrayDumpContext
import com.tans.thprofparser.records.subrecorders.PrimitiveArrayDumpContext
import com.tans.thprofparser.records.subrecorders.RefRootContext
import com.tans.thprofparser.records.subrecorders.StackRootContext
import com.tans.thprofparser.records.subrecorders.SubRecorderType
import com.tans.thprofparser.records.subrecorders.ThreadRootContext
import okio.BufferedSource
import okio.buffer
import okio.source
import java.io.ByteArrayInputStream
import java.io.InputStream

class HprofReader(inputStream: InputStream) {

    private val source: BufferedSource = inputStream.source().buffer()
    private val header: HprofHeader

    init {
        val endOfVersionStrIndex = source.indexOf(0x00)
        val versionStr = source.readUtf8(endOfVersionStrIndex)
        val version = HprofVersion.entries.find { it.versionString == versionStr }
        if (version == null) {
            throw HprofParserException("Unknown hprof version: $versionStr")
        }
        source.skip(1)
        val identifierBytesSize = source.readInt()
        val heapDumpTimeStamp = source.readLong()
        header = HprofHeader(
            version = version,
            identifierByteSize = identifierBytesSize,
            heapDumpTimestamp = heapDumpTimeStamp
        )
    }

    fun accept(
        hprofVisitor: HprofVisitor
    ) {
        hprofVisitor.visitHeader(header)

        while (!source.exhausted()) {
            val tag = source.readUnsignedByte()
            val timestamp = source.readUnsignedInt()
            val bodyLen = source.readUnsignedInt()
            val recordContext = RecordContext(
                tag = tag,
                timestamp = timestamp,
                bodyLength = bodyLen,
                header = header
            )
            when (tag) {
                RecordType.STRING_IN_UTF8.tag -> {
                    readStringRecorder(recordContext, hprofVisitor)
                }
                RecordType.LOAD_CLASS.tag -> {
                    readLoadClassRecorder(recordContext, hprofVisitor)
                }
                RecordType.UNLOAD_CLASS.tag -> {
                    readUnloadClassRecorder(recordContext, hprofVisitor)
                }
                RecordType.STACK_FRAME.tag -> {
                    readStackFrameRecorder(recordContext, hprofVisitor)
                }
                RecordType.STACK_TRACE.tag -> {
                    readStackTraceRecorder(recordContext, hprofVisitor)
                }
                RecordType.HEAP_DUMP.tag -> {
                    readHeapDumpRecorder(recordContext, hprofVisitor)
                }
                RecordType.HEAP_DUMP_SEGMENT.tag -> {
                    readHeapDumpRecorder(recordContext, hprofVisitor)
                }
                RecordType.HEAP_DUMP_END.tag -> {
                    break
                }
                else -> {
                    readUnknownRecorder(recordContext, hprofVisitor)
                }
            }
        }
        hprofVisitor.visitEnd()
    }

    private fun readStringRecorder(
        recordContext: RecordContext,
        hprofVisitor: HprofVisitor
    ) {
        val id = source.readId(header)
        val str = source.readString(
            recordContext.bodyLength - header.identifierByteSize,
            Charsets.UTF_8
        )
        hprofVisitor.visitStringRecord(StringContext(recordContext = recordContext, id = id, str = str))
    }

    private fun readLoadClassRecorder(
        recordContext: RecordContext,
        hprofVisitor: HprofVisitor
    ) {
        val classSerialNumber = source.readUnsignedInt()
        val id = source.readId(header)
        val stackTraceSerialNumber = source.readUnsignedInt()
        val classNameStringId = source.readId(header)
        hprofVisitor.visitLoadClassRecord(
            LoadClassContext(
                recordContext = recordContext,
                classSerialNumber = classSerialNumber,
                id = id,
                stackTraceSerialNumber = stackTraceSerialNumber,
                classNameStringId = classNameStringId
            )
        )
    }

    private fun readUnloadClassRecorder(
        recordContext: RecordContext,
        hprofVisitor: HprofVisitor
    ) {
        val classSerialNumber = source.readUnsignedInt()
        hprofVisitor.visitUnloadClassRecord(
            UnloadClassContext(
                recordContext = recordContext,
                classSerialNumber = classSerialNumber
            )
        )
    }

    private fun readStackFrameRecorder(
        recordContext: RecordContext,
        hprofVisitor: HprofVisitor
    ) {
        val id = source.readId(header)
        val methodNameStringId = source.readId(header)
        val methodSignatureStringId = source.readId(header)
        val sourceFileNameStringId = source.readId(header)
        val classSerialNumber = source.readUnsignedInt()
        val lineNumber = source.readUnsignedInt()
        hprofVisitor.visitStackFrameRecord(
            StackFrameContext(
                recordContext = recordContext,
                id = id,
                methodNameStringId = methodNameStringId,
                methodSignatureStringId = methodSignatureStringId,
                sourceFileNameStringId = sourceFileNameStringId,
                classSerialNumber = classSerialNumber,
                lineNumber = lineNumber
            )
        )
    }

    private fun readStackTraceRecorder(
        recordContext: RecordContext,
        hprofVisitor: HprofVisitor
    ) {
        val stackTraceSerialNumber = source.readUnsignedInt()
        val threadSerialNumber = source.readUnsignedInt()
        val stackFrameSize = source.readUnsignedInt()
        val stackFrameIds = ArrayList<Long>()
        repeat(stackFrameSize.toInt()) {
            stackFrameIds.add(source.readId(header))
        }
        hprofVisitor.visitStackTraceRecord(
            StackTraceContext(
                recordContext = recordContext,
                stackTraceSerialNumber = stackTraceSerialNumber,
                threadSerialNumber = threadSerialNumber,
                stackFrameIds = stackFrameIds
            )
        )
    }

    private fun readHeapDumpRecorder(
        recordContext: RecordContext,
        hprofVisitor: HprofVisitor
    ) {
        val heapDumpVisitor = hprofVisitor.visitHeapDumpRecorder(recordContext)
        ByteArrayInputStream(source.readByteArray(recordContext.bodyLength)).source().buffer().use { subSource ->
            while (!subSource.exhausted()) {
                val subTag = subSource.readUnsignedByte()
                val heapDumpContext = HeapDumpContext(
                    recordContext = recordContext,
                    subTag = subTag
                )
                when (subTag) {
                    SubRecorderType.ROOT_UNKNOWN.tag -> {
                        val id = subSource.readId(header)
                        heapDumpVisitor?.visitRootUnknownSubRecord(
                            IdRootContext(heapDumpContext, id)
                        )
                    }
                    SubRecorderType.ROOT_JNI_GLOBAL.tag -> {
                        val id = subSource.readId(header)
                        val refId = subSource.readId(header)
                        heapDumpVisitor?.visitRootJniGlobalSubRecord(
                            RefRootContext(heapDumpContext, id, refId)
                        )
                    }
                    SubRecorderType.ROOT_JNI_LOCAL.tag -> {
                        val id = subSource.readId(header)
                        val threadSerialNumber = subSource.readUnsignedInt()
                        val frameNumber = subSource.readUnsignedInt()
                        heapDumpVisitor?.visitRootJniLocalSubRecord(
                            FrameRootContext(heapDumpContext, id, threadSerialNumber, frameNumber)
                        )
                    }
                    SubRecorderType.ROOT_JAVA_FRAME.tag -> {
                        val id = subSource.readId(header)
                        val threadSerialNumber = subSource.readUnsignedInt()
                        val frameNumber = subSource.readUnsignedInt()
                        heapDumpVisitor?.visitRootJavaFrameSubRecord(
                            FrameRootContext(heapDumpContext, id, threadSerialNumber, frameNumber)
                        )
                    }
                    SubRecorderType.ROOT_NATIVE_STACK.tag -> {
                        val id = subSource.readId(header)
                        val threadSerialNumber = subSource.readUnsignedInt()
                        heapDumpVisitor?.visitRootNativeStackSubRecord(
                            ThreadRootContext(heapDumpContext, id, threadSerialNumber)
                        )
                    }
                    SubRecorderType.ROOT_STICKY_CLASS.tag -> {
                        val id = subSource.readId(header)
                        heapDumpVisitor?.visitRootStickyClassSubRecord(
                            IdRootContext(heapDumpContext, id)
                        )
                    }
                    SubRecorderType.ROOT_THREAD_BLOCK.tag -> {
                        val id = subSource.readId(header)
                        val threadSerialNumber = subSource.readUnsignedInt()
                        heapDumpVisitor?.visitRootThreadBlockSubRecord(
                            ThreadRootContext(heapDumpContext, id, threadSerialNumber)
                        )
                    }
                    SubRecorderType.ROOT_MONITOR_USED.tag -> {
                        val id = subSource.readId(header)
                        heapDumpVisitor?.visitRootMonitorUsedSubRecord(
                            IdRootContext(heapDumpContext, id)
                        )
                    }
                    SubRecorderType.ROOT_THREAD_OBJECT.tag -> {
                        val id = subSource.readId(header)
                        val threadSerialNumber = subSource.readUnsignedInt()
                        val frameNumber = subSource.readUnsignedInt()
                        heapDumpVisitor?.visitRootThreadObjectSubRecord(
                            FrameRootContext(heapDumpContext, id, threadSerialNumber, frameNumber)
                        )
                    }
                    SubRecorderType.ROOT_INTERNED_STRING.tag -> {
                        val id = subSource.readId(header)
                        heapDumpVisitor?.visitRootInternedStringSubRecord(
                            IdRootContext(heapDumpContext, id)
                        )
                    }
                    SubRecorderType.ROOT_FINALIZING.tag -> {
                        val id = subSource.readId(header)
                        heapDumpVisitor?.visitRootFinalizingSubRecord(
                            IdRootContext(heapDumpContext, id)
                        )
                    }
                    SubRecorderType.ROOT_DEBUGGER.tag -> {
                        val id = subSource.readId(header)
                        heapDumpVisitor?.visitRootDebuggerSubRecord(
                            IdRootContext(heapDumpContext, id)
                        )
                    }
                    SubRecorderType.ROOT_REFERENCE_CLEANUP.tag -> {
                        val id = subSource.readId(header)
                        heapDumpVisitor?.visitRootReferenceCleanupSubRecord(
                            IdRootContext(heapDumpContext, id)
                        )
                    }
                    SubRecorderType.ROOT_VM_INTERNAL.tag -> {
                        val id = subSource.readId(header)
                        heapDumpVisitor?.visitRootVmInternalSubRecord(
                            IdRootContext(heapDumpContext, id)
                        )
                    }
                    SubRecorderType.ROOT_JNI_MONITOR.tag -> {
                        val id = subSource.readId(header)
                        val threadSerialNumber = subSource.readUnsignedInt()
                        val stackDepth = subSource.readUnsignedInt()
                        heapDumpVisitor?.visitRootJniMonitorSubRecord(
                            StackRootContext(heapDumpContext, id, threadSerialNumber, stackDepth)
                        )
                    }
                    SubRecorderType.ROOT_UNREACHABLE.tag -> {
                        val id = subSource.readId(header)
                        heapDumpVisitor?.visitRootUnreachableSubRecord(
                            IdRootContext(heapDumpContext, id)
                        )
                    }

                    SubRecorderType.CLASS_DUMP.tag -> {
                        val id = subSource.readId(header)
                        val stackTraceSerialNumber = subSource.readUnsignedInt()
                        val superClassId = subSource.readId(header)
                        val classLoaderId = subSource.readId(header)
                        val signerId = subSource.readId(header)
                        val protectionDomainId = subSource.readId(header)
                        val unknownId1 = subSource.readId(header)
                        val unknownId2 = subSource.readId(header)
                        val instanceSize = subSource.readUnsignedInt()
                        val constFieldSize = subSource.readUnsignedShort()
                        val constFields = ArrayList<ConstField>()
                        repeat(constFieldSize) {
                            constFields.add(subSource.readConstField(header))
                        }
                        val staticFieldSize = subSource.readUnsignedShort()
                        val staticFields = ArrayList<StaticField>()
                        repeat(staticFieldSize) {
                            staticFields.add(subSource.readStaticField(header))
                        }
                        val memberFieldSize = subSource.readUnsignedShort()
                        val memberFields = ArrayList<MemberField>()
                        repeat(memberFieldSize) {
                            memberFields.add(subSource.readMemberField(header))
                        }
                        heapDumpVisitor?.visitClassDumpSubRecord(
                            ClassDumpContext(
                                heapDumpContext,
                                id,
                                stackTraceSerialNumber,
                                superClassId,
                                classLoaderId,
                                signerId,
                                protectionDomainId,
                                unknownId1,
                                unknownId2,
                                instanceSize,
                                constFields,
                                staticFields,
                                memberFields
                            )
                        )
                    }
                    SubRecorderType.INSTANCE_DUMP.tag -> {
                        val id = subSource.readId(header)
                        val stackTraceSerialNumber = subSource.readUnsignedInt()
                        val classId = subSource.readId(header)
                        val contentSize = subSource.readUnsignedInt()
                        val contentBytes = subSource.readByteArray(contentSize)
                        heapDumpVisitor?.visitInstanceDumpSubRecord(
                            InstanceDumpContext(
                                heapDumpContext,
                                id,
                                stackTraceSerialNumber,
                                classId,
                                contentBytes
                            )
                        )
                    }

                    SubRecorderType.OBJECT_ARRAY_DUMP.tag -> {
                        val id = subSource.readId(header)
                        val stackTraceSerialNumber = subSource.readUnsignedInt()
                        val arrayLength = subSource.readUnsignedInt()
                        val arrayClassId = subSource.readId(header)
                        val elementIds = ArrayList<Long>()
                        repeat(arrayLength.toInt()) {
                            elementIds.add(subSource.readId(header))
                        }
                        heapDumpVisitor?.visitObjectArrayDumpSubRecord(
                            ObjectArrayDumpContext(
                                heapDumpContext,
                                id,
                                stackTraceSerialNumber,
                                arrayClassId,
                                elementIds
                            )
                        )
                    }

                    SubRecorderType.PRIMITIVE_ARRAY_DUMP.tag -> {
                        val id = subSource.readId(header)
                        val stackTraceSerialNumber = subSource.readUnsignedInt()
                        val arrayLength = subSource.readUnsignedInt()
                        val type = subSource.readUnsignedByte()
                        val elements = ArrayList<ValueHolder>()
                        repeat(arrayLength.toInt()) {
                            elements.add(subSource.readValue(type, header))
                        }
                        heapDumpVisitor?.visitPrimitiveArrayDumpSubRecord(
                            PrimitiveArrayDumpContext(
                                heapDumpContext,
                                id,
                                stackTraceSerialNumber,
                                type,
                                elements
                            )
                        )
                    }


                    SubRecorderType.HEAP_DUMP_INFO.tag -> {
                        val heapId = subSource.readId(header)
                        val stringId = subSource.readId(header)
                        heapDumpVisitor?.visitHeapDumpInfoSubRecord(
                            HeapDumpInfoContext(heapDumpContext, heapId, stringId)
                        )
                    }

                    else -> {
                        break
                    }
                }
            }
            heapDumpVisitor?.visitEnd()
        }
    }

    private fun readUnknownRecorder(
        recordContext: RecordContext,
        hprofVisitor: HprofVisitor
    ) {
        val bytes = source.readByteArray(recordContext.bodyLength)
        hprofVisitor.visitUnknownRecord(UnknownContext(
            recordContext = recordContext,
            body = bytes
        ))
    }

}