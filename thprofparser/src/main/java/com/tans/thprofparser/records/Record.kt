package com.tans.thprofparser.records

import com.tans.thprofparser.HprofHeader
import com.tans.thprofparser.records.subrecorders.SubRecord

sealed class Record {

    abstract val tag: Int
    abstract val timestamp: Long
    abstract val bodyLen: Long

    abstract fun calculateBodyLen(header: HprofHeader): Long

    data class StringRecord(
        override val tag: Int = RecordType.STRING_IN_UTF8.tag,
        override val timestamp: Long,
        override val bodyLen: Long,
        val id: Long,
        val string: String
    ) : Record() {

        override fun calculateBodyLen(header: HprofHeader): Long {
            return string.toByteArray(Charsets.UTF_8).size + header.identifierByteSize.toLong()
        }
    }

    data class LoadClassRecord(
        override val tag: Int = RecordType.LOAD_CLASS.tag,
        override val timestamp: Long,
        override val bodyLen: Long,
        val classSerialNumber: Long,
        val id: Long,
        val stackTraceSerialNumber: Long,
        val classNameStringId: Long
    ) : Record() {

        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong() * 2 + 4 * 2
        }
    }

    data class UnloadClassRecord(
        override val tag: Int = RecordType.UNLOAD_CLASS.tag,
        override val timestamp: Long,
        override val bodyLen: Long,
        val classSerialNumber: Long
    ) : Record() {

        override fun calculateBodyLen(header: HprofHeader): Long {
            return 4
        }
    }

    data class StackFrameRecord(
        override val tag: Int = RecordType.STACK_FRAME.tag,
        override val timestamp: Long,
        override val bodyLen: Long,
        val id: Long,
        val methodNameStringId: Long,
        val methodSignatureStringId: Long,
        val sourceFileNameStringId: Long,
        val classSerialNumber: Long,
        val lineNumber: Long
    ) : Record() {

        override fun calculateBodyLen(header: HprofHeader): Long {
            return header.identifierByteSize.toLong() * 4 + 4 * 2
        }
    }

    data class StackTraceRecord(
        override val tag: Int = RecordType.STACK_TRACE.tag,
        override val timestamp: Long,
        override val bodyLen: Long,
        val stackTraceSerialNumber: Long,
        val threadSerialNumber: Long,
        val stackFrameIds: List<Long>
    ) : Record() {

        override fun calculateBodyLen(header: HprofHeader): Long {
            return 4 * 3 + header.identifierByteSize.toLong() * stackFrameIds.size
        }
    }

    data class HeapDumpRecord(
        override val tag: Int,
        override val timestamp: Long,
        override val bodyLen: Long,
        val subRecords: List<SubRecord>
    ) : Record() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            var bodyLen = 0L
            for (r in subRecords) {
                bodyLen += r.calculateBodyLen(header) + 1
            }
            return bodyLen
        }
    }

    data class UnknownRecord(
        override val tag: Int,
        override val timestamp: Long,
        override val bodyLen: Long,
        val contentBytes: ByteArray
    ) : Record() {
        override fun calculateBodyLen(header: HprofHeader): Long {
            return contentBytes.size.toLong()
        }
    }
}