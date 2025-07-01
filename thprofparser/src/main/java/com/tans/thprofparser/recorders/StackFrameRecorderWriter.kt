package com.tans.thprofparser.recorders

import com.tans.thprofparser.HprofHeader
import com.tans.thprofparser.HprofParserException
import com.tans.thprofparser.writeId
import com.tans.thprofparser.writeUnsignedByte
import com.tans.thprofparser.writeUnsignedInt
import okio.BufferedSink

internal class StackFrameRecorderWriter(
    tag: Int,
    timeStamp: Long,
    header: HprofHeader,
    private val sink: BufferedSink
) : StackFrameRecorderVisitor(tag, timeStamp, header) {

    private var id: Long? = null
    private var methodNameStringId: Long? = null
    private var methodSignatureStringId: Long? = null
    private var sourceFileNameStringId: Long? = null
    private var classSerialNumber: Long? = null
    private var lineNumber: Long? = null

    override fun visitStackFrame(
        id: Long,
        methodNameStringId: Long,
        methodSignatureStringId: Long,
        sourceFileNameStringId: Long,
        classSerialNumber: Long,
        lineNumber: Long
    ) {
        this.id = id
        this.methodNameStringId = methodNameStringId
        this.methodSignatureStringId = methodSignatureStringId
        this.sourceFileNameStringId = sourceFileNameStringId
        this.classSerialNumber = classSerialNumber
        this.lineNumber = lineNumber
    }

    override fun visitEnd() {
        val id: Long? = this.id
        val methodNameStringId: Long? = this.methodNameStringId
        val methodSignatureStringId: Long? = this.methodSignatureStringId
        val sourceFileNameStringId: Long? = this.sourceFileNameStringId
        val classSerialNumber: Long? = this.classSerialNumber
        val lineNumber: Long? = this.lineNumber
        if (id == null || methodNameStringId == null || methodSignatureStringId == null ||
            sourceFileNameStringId == null || classSerialNumber == null || lineNumber == null) {
            throw HprofParserException("Do not invoke visitStackFrame() method.")
        }
        sink.writeUnsignedByte(tag)
        sink.writeUnsignedInt(timeStamp)
        sink.writeUnsignedInt((header.identifierByteSize * 4 + 4 * 2).toLong())
        sink.writeId(id, header)
        sink.writeId(methodNameStringId, header)
        sink.writeId(methodSignatureStringId, header)
        sink.writeId(sourceFileNameStringId, header)
        sink.writeUnsignedInt(classSerialNumber)
        sink.writeUnsignedInt(lineNumber)
    }
}