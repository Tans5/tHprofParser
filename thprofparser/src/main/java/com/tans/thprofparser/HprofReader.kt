package com.tans.thprofparser

import com.tans.thprofparser.recorders.RecorderType
import okio.BufferedSource
import okio.buffer
import okio.source
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
        hprofVisitor.visit(header)

        while (!source.exhausted()) {
            val tag = source.readUnsignedByte()
            val timeStamp = source.readUnsignedInt()
            val bodyLen = source.readUnsignedInt()
            when (tag) {
                RecorderType.STRING_IN_UTF8.tag -> {
                    readStringRecorder(tag, timeStamp, bodyLen, hprofVisitor)
                }
                RecorderType.LOAD_CLASS.tag -> {
                    readLoadClassRecorder(tag, timeStamp, bodyLen, hprofVisitor)
                }
                RecorderType.UNLOAD_CLASS.tag -> {
                    readUnloadClassRecorder(tag, timeStamp, bodyLen, hprofVisitor)
                }
                RecorderType.STACK_FRAME.tag -> {
                    readStackFrameRecorder(tag, timeStamp, bodyLen, hprofVisitor)
                }
                RecorderType.STACK_TRACE.tag -> {
                    readStackTraceRecorder(tag, timeStamp, bodyLen, hprofVisitor)
                }
                RecorderType.HEAP_DUMP.tag -> {
                    // FIXME:
                    readUnknownRecorder(tag, timeStamp, bodyLen, hprofVisitor)
                    // readHeapDumpRecorder(tag, timeStamp, bodyLen, hprofVisitor)
                }
                RecorderType.HEAP_DUMP_SEGMENT.tag -> {
                    // FIXME:
                    readUnknownRecorder(tag, timeStamp, bodyLen, hprofVisitor)
                    // readHeapDumpSegmentRecorder(tag, timeStamp, bodyLen, hprofVisitor)
                }
                RecorderType.HEAP_DUMP_END.tag -> {
                    break
                }
                else -> {
                    readUnknownRecorder(tag, timeStamp, bodyLen, hprofVisitor)
                }
            }
        }
        hprofVisitor.visitEnd()
    }

    private fun readStringRecorder(
        tag: Int,
        timeStamp: Long,
        bodyLen: Long,
        hprofVisitor: HprofVisitor) {
        val id = source.readId(header)
        val str = source.readString(bodyLen - header.identifierByteSize, Charsets.UTF_8)
        val recorderVisitor = hprofVisitor.visitStringRecorder(
            tag,
            timeStamp,
            header
        )
        recorderVisitor?.visitString(id, str)
        recorderVisitor?.visitEnd()
    }

    private fun readLoadClassRecorder(
        tag: Int,
        timeStamp: Long,
        bodyLen: Long,
        hprofVisitor: HprofVisitor
    ) {
        val classSerialNumber = source.readUnsignedInt()
        val id = source.readId(header)
        val stackTraceSerialNumber = source.readUnsignedInt()
        val classNameStringId = source.readId(header)
        val recorderVisitor = hprofVisitor.visitLoadClassRecorder(tag, timeStamp, header)
        recorderVisitor?.visitLoadClass(classSerialNumber, id, stackTraceSerialNumber, classNameStringId)
        recorderVisitor?.visitEnd()
    }

    private fun readUnloadClassRecorder(
        tag: Int,
        timeStamp: Long,
        bodyLen: Long,
        hprofVisitor: HprofVisitor
    ) {
        val classSerialNumber = source.readUnsignedInt()
        val unloadClassVisitor = hprofVisitor.visitUnloadClassRecorder(tag, timeStamp, header)
        unloadClassVisitor?.visitUnloadClassRecorder(classSerialNumber)
        unloadClassVisitor?.visitEnd()
    }

    private fun readStackFrameRecorder(
        tag: Int,
        timeStamp: Long,
        bodyLen: Long,
        hprofVisitor: HprofVisitor
    ) {
        val id = source.readId(header)
        val methodNameStringId = source.readId(header)
        val methodSignatureStringId = source.readId(header)
        val sourceFileNameStringId = source.readId(header)
        val classSerialNumber = source.readUnsignedInt()
        val lineNumber = source.readUnsignedInt()
        val stackFrameVisitor = hprofVisitor.visitStackFrameRecorder(tag, timeStamp, header)
        stackFrameVisitor?.visitStackFrame(id, methodNameStringId, methodSignatureStringId, sourceFileNameStringId,
            classSerialNumber, lineNumber)
        stackFrameVisitor?.visitEnd()
    }

    private fun readStackTraceRecorder(
        tag: Int,
        timeStamp: Long,
        bodyLen: Long,
        hprofVisitor: HprofVisitor
    ) {
        val stackTraceSerialNumber = source.readUnsignedInt()
        val threadSerialNumber = source.readUnsignedInt()
        val stackFrameSize = source.readUnsignedInt()
        val stackFrameIds = ArrayList<Long>()
        repeat(stackFrameSize.toInt()) {
            stackFrameIds.add(source.readId(header))
        }
        val stackTraceVisitor = hprofVisitor.visitStackTraceRecorder(tag, timeStamp, header)
        stackTraceVisitor?.visitStackTrace(stackTraceSerialNumber, threadSerialNumber, stackFrameIds)
        stackTraceVisitor?.visitEnd()
    }

    private fun readHeapDumpRecorder(
        tag: Int,
        timeStamp: Long,
        bodyLen: Long,
        hprofVisitor: HprofVisitor
    ) {
        // TODO:
    }

    private fun readHeapDumpSegmentRecorder(
        tag: Int,
        timeStamp: Long,
        bodyLen: Long,
        hprofVisitor: HprofVisitor
    ) {
        // TODO:
    }

    private fun readUnknownRecorder(
        tag: Int,
        timeStamp: Long,
        bodyLen: Long,
        hprofVisitor: HprofVisitor
    ) {
        val bytes = source.readByteArray(bodyLen)

    }

}