package com.tans.thprofparser.records.subrecorders

import com.tans.thprofparser.ConstField
import com.tans.thprofparser.HprofHeader
import com.tans.thprofparser.MemberField
import com.tans.thprofparser.StaticField
import com.tans.thprofparser.ValueHolder

sealed class SubRecord {

    abstract val subTag: Int

    abstract fun calculateBodyLen(header: HprofHeader): Long

    data class RootUnknownSubRecord(
        override val subTag: Int = SubRecordType.ROOT_UNKNOWN.tag,
        val id: Long
    ) : SubRecord() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong()
        }
    }

    data class RootJniGlobalSubRecord(
        override val subTag: Int = SubRecordType.ROOT_JNI_GLOBAL.tag,
        val id: Long,
        val refId: Long
    ) : SubRecord() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong() * 2
        }
    }


    data class RootJniLocalSubRecord(
        override val subTag: Int = SubRecordType.ROOT_JNI_LOCAL.tag,
        val id: Long,
        val threadSerialNumber: Long,
        val frameNumber: Long
    ) : SubRecord() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong() + 4 * 2
        }
    }

    data class RootJavaFrameSubRecord(
        override val subTag: Int = SubRecordType.ROOT_JAVA_FRAME.tag,
        val id: Long,
        val threadSerialNumber: Long,
        val frameNumber: Long
    ) : SubRecord() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong() + 4 * 2
        }
    }

    data class RootNativeStackSubRecord(
        override val subTag: Int = SubRecordType.ROOT_NATIVE_STACK.tag,
        val id: Long,
        val threadSerialNumber: Long,
    ) : SubRecord() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong() + 4
        }
    }

    data class RootStickyClassSubRecord(
        override val subTag: Int = SubRecordType.ROOT_STICKY_CLASS.tag,
        val id: Long,
    ) : SubRecord() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong()
        }
    }

    data class RootThreadBlockSubRecord(
        override val subTag: Int = SubRecordType.ROOT_THREAD_BLOCK.tag,
        val id: Long,
        val threadSerialNumber: Long,
    ) : SubRecord() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong() + 4
        }
    }

    data class RootMonitorUsedSubRecord(
        override val subTag: Int = SubRecordType.ROOT_MONITOR_USED.tag,
        val id: Long,
    ) : SubRecord() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong()
        }
    }

    data class RootThreadObjectSubRecord(
        override val subTag: Int = SubRecordType.ROOT_THREAD_OBJECT.tag,
        val id: Long,
        val threadSerialNumber: Long,
        val frameNumber: Long
    ) : SubRecord() {

        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong() + 4 * 2
        }
    }

    data class RootInternedStringSubRecord(
        override val subTag: Int = SubRecordType.ROOT_INTERNED_STRING.tag,
        val id: Long,
    ) : SubRecord() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong()
        }
    }

    data class RootFinalizingSubRecord(
        override val subTag: Int = SubRecordType.ROOT_FINALIZING.tag,
        val id: Long,
    ) : SubRecord() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong()
        }
    }

    data class RootDebuggerSubRecord(
        override val subTag: Int = SubRecordType.ROOT_DEBUGGER.tag,
        val id: Long,
    ) : SubRecord() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong()
        }
    }

    data class RootReferenceCleanupSubRecord(
        override val subTag: Int = SubRecordType.ROOT_REFERENCE_CLEANUP.tag,
        val id: Long,
    ) : SubRecord() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong()
        }
    }

    data class RootVmInternalSubRecord(
        override val subTag: Int = SubRecordType.ROOT_VM_INTERNAL.tag,
        val id: Long,
    ) : SubRecord() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong()
        }
    }

    data class RootJniMonitorSubRecord(
        override val subTag: Int = SubRecordType.ROOT_JNI_MONITOR.tag,
        val id: Long,
        val threadSerialNumber: Long,
        val stackDepth: Long
    ) : SubRecord() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong() + 4 * 2
        }
    }

    data class RootUnreachableSubRecord(
        override val subTag: Int = SubRecordType.ROOT_UNREACHABLE.tag,
        val id: Long,
    ) : SubRecord() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong()
        }
    }

    data class HeapDumpInfoSubRecord(
        override val subTag: Int = SubRecordType.HEAP_DUMP_INFO.tag,
        val heapId: Long,
        val stringId: Long
    ) : SubRecord() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong() * 2
        }
    }

    data class ClassDumpSubRecord(
        override val subTag: Int = SubRecordType.CLASS_DUMP.tag,
        val id: Long,
        val stackTraceSerialNumber: Long,
        val superClassId: Long,
        val classLoaderId: Long,
        val signerId: Long,
        val protectionDomainId: Long,
        val unknownId1: Long,
        val unknownId2: Long,
        val instanceSize: Long,
        val constFields: List<ConstField>,
        val staticFields: List<StaticField>,
        val memberFields: List<MemberField>
    ) : SubRecord() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            val idSize = header.identifierByteSize.toLong()
            var bodyLen = idSize * 7 + 4 * 2 + 2 * 3
            for (f in constFields) {
                bodyLen += f.value.size + 4 + 1
            }
            for (f in staticFields) {
                bodyLen += f.value.size + idSize + 1
            }
            bodyLen += memberFields.size * (idSize + 1)
            return bodyLen
        }
    }

    data class InstanceDumpSubRecord(
        override val subTag: Int = SubRecordType.INSTANCE_DUMP.tag,
        val id: Long,
        val stackTraceSerialNumber: Long,
        val classId: Long,
        val contentBytes: ByteArray
    ) : SubRecord() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong() * 2 + 4 * 2 + contentBytes.size
        }
    }

    data class ObjectArrayDumpSubRecord(
        override val subTag: Int = SubRecordType.OBJECT_ARRAY_DUMP.tag,
        val id: Long,
        val stackTraceSerialNumber: Long,
        val arrayClassId: Long,
        val elementIds: List<Long>
    ) : SubRecord() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong() * (2 + elementIds.size) + 4 * 2
        }
    }

    data class PrimitiveArrayDumpSubRecord(
        override val subTag: Int = SubRecordType.PRIMITIVE_ARRAY_DUMP.tag,
        val id: Long,
        val stackTraceSerialNumber: Long,
        val type: Int,
        val elements: List<ValueHolder>
    ) : SubRecord() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            var bodyLen = header.identifierByteSize.toLong() + 4 * 2 + 1
            for (e in elements) {
                bodyLen += e.size
            }
            return bodyLen
        }
    }
}