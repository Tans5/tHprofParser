package com.tans.thprofparser

import com.tans.thprofparser.recorders.LoadClassRecorderVisitor
import com.tans.thprofparser.recorders.RecorderContext
import com.tans.thprofparser.recorders.RecorderType
import com.tans.thprofparser.recorders.StackFrameRecorderVisitor
import com.tans.thprofparser.recorders.StackTraceRecorderVisitor
import com.tans.thprofparser.recorders.StringRecorderVisitor
import com.tans.thprofparser.recorders.UnknownRecorderVisitor
import com.tans.thprofparser.recorders.UnloadClassRecorderVisitor
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
            val timestamp = source.readUnsignedInt()
            val bodyLen = source.readUnsignedInt()
            val recorderContext = RecorderContext(
                tag = tag,
                timestamp = timestamp,
                bodyLength = bodyLen,
                header = header
            )
            when (tag) {
                RecorderType.STRING_IN_UTF8.tag -> {
                    readStringRecorder(recorderContext, hprofVisitor)
                }
                RecorderType.LOAD_CLASS.tag -> {
                    readLoadClassRecorder(recorderContext, hprofVisitor)
                }
                RecorderType.UNLOAD_CLASS.tag -> {
                    readUnloadClassRecorder(recorderContext, hprofVisitor)
                }
                RecorderType.STACK_FRAME.tag -> {
                    readStackFrameRecorder(recorderContext, hprofVisitor)
                }
                RecorderType.STACK_TRACE.tag -> {
                    readStackTraceRecorder(recorderContext, hprofVisitor)
                }
                RecorderType.HEAP_DUMP.tag -> {
                    // FIXME:
                    readUnknownRecorder(recorderContext, hprofVisitor)
                    // readHeapDumpRecorder(tag, timeStamp, bodyLen, hprofVisitor)
                }
                RecorderType.HEAP_DUMP_SEGMENT.tag -> {
                    // FIXME:
                    readUnknownRecorder(recorderContext, hprofVisitor)
                    // readHeapDumpSegmentRecorder(tag, timeStamp, bodyLen, hprofVisitor)
                }
                RecorderType.HEAP_DUMP_END.tag -> {
                    break
                }
                else -> {
                    readUnknownRecorder(recorderContext, hprofVisitor)
                }
            }
        }
        hprofVisitor.visitEnd()
    }

    private fun readStringRecorder(
        recorderContext: RecorderContext,
        hprofVisitor: HprofVisitor
    ) {
        val id = source.readId(header)
        val str = source.readString(
            recorderContext.bodyLength - header.identifierByteSize,
            Charsets.UTF_8
        )
        val recorderVisitor = hprofVisitor.visitStringRecorder(recorderContext)
        recorderVisitor?.visitString(StringRecorderVisitor.Companion.StringContext(
            recorderContext = recorderContext,
            id = id,
            str = str
        ))
        recorderVisitor?.visitEnd()
    }

    private fun readLoadClassRecorder(
        recorderContext: RecorderContext,
        hprofVisitor: HprofVisitor
    ) {
        val classSerialNumber = source.readUnsignedInt()
        val id = source.readId(header)
        val stackTraceSerialNumber = source.readUnsignedInt()
        val classNameStringId = source.readId(header)
        val recorderVisitor = hprofVisitor.visitLoadClassRecorder(recorderContext)
        recorderVisitor?.visitLoadClass(LoadClassRecorderVisitor.Companion.LoadClassContext(
            recorderContext = recorderContext,
            classSerialNumber = classSerialNumber,
            id = id,
            stackTraceSerialNumber = stackTraceSerialNumber,
            classNameStringId = classNameStringId
        ))
        recorderVisitor?.visitEnd()
    }

    private fun readUnloadClassRecorder(
        recorderContext: RecorderContext,
        hprofVisitor: HprofVisitor
    ) {
        val classSerialNumber = source.readUnsignedInt()
        val unloadClassVisitor = hprofVisitor.visitUnloadClassRecorder(recorderContext)
        unloadClassVisitor?.visitUnloadClassRecorder(UnloadClassRecorderVisitor.Companion.UnloadClassContext(
            recorderContext = recorderContext,
            classSerialNumber = classSerialNumber
        ))
        unloadClassVisitor?.visitEnd()
    }

    private fun readStackFrameRecorder(
        recorderContext: RecorderContext,
        hprofVisitor: HprofVisitor
    ) {
        val id = source.readId(header)
        val methodNameStringId = source.readId(header)
        val methodSignatureStringId = source.readId(header)
        val sourceFileNameStringId = source.readId(header)
        val classSerialNumber = source.readUnsignedInt()
        val lineNumber = source.readUnsignedInt()
        val stackFrameVisitor = hprofVisitor.visitStackFrameRecorder(recorderContext)
        stackFrameVisitor?.visitStackFrame(StackFrameRecorderVisitor.Companion.StackFrameContext(
            recorderContext = recorderContext,
            id = id,
            methodNameStringId = methodNameStringId,
            methodSignatureStringId = methodSignatureStringId,
            sourceFileNameStringId = sourceFileNameStringId,
            classSerialNumber = classSerialNumber,
            lineNumber = lineNumber
        ))
        stackFrameVisitor?.visitEnd()
    }

    private fun readStackTraceRecorder(
        recorderContext: RecorderContext,
        hprofVisitor: HprofVisitor
    ) {
        val stackTraceSerialNumber = source.readUnsignedInt()
        val threadSerialNumber = source.readUnsignedInt()
        val stackFrameSize = source.readUnsignedInt()
        val stackFrameIds = ArrayList<Long>()
        repeat(stackFrameSize.toInt()) {
            stackFrameIds.add(source.readId(header))
        }
        val stackTraceVisitor = hprofVisitor.visitStackTraceRecorder(recorderContext)
        stackTraceVisitor?.visitStackTrace(StackTraceRecorderVisitor.Companion.StackTraceContext(
            recorderContext = recorderContext,
            stackTraceSerialNumber = stackTraceSerialNumber,
            threadSerialNumber = threadSerialNumber,
            stackFrameIds = stackFrameIds
        ))
        stackTraceVisitor?.visitEnd()
    }

    private fun readHeapDumpRecorder(
        recorderContext: RecorderContext,
        hprofVisitor: HprofVisitor
    ) {
        // TODO:
    }

    private fun readHeapDumpSegmentRecorder(
        recorderContext: RecorderContext,
        hprofVisitor: HprofVisitor
    ) {
        // TODO:
    }

    private fun readUnknownRecorder(
        recorderContext: RecorderContext,
        hprofVisitor: HprofVisitor
    ) {
        val bytes = source.readByteArray(recorderContext.bodyLength)
        val visitor = hprofVisitor.visitUnknownRecorder(recorderContext)
        visitor?.visitBody(UnknownRecorderVisitor.Companion.UnknownBodyContext(
            recorderContext = recorderContext,
            body = bytes
        ))
        visitor?.visitEnd()
    }

}