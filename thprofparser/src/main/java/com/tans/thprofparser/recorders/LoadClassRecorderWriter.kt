package com.tans.thprofparser.recorders

import com.tans.thprofparser.HprofHeader
import com.tans.thprofparser.HprofParserException
import com.tans.thprofparser.writeId
import com.tans.thprofparser.writeUnsignedByte
import com.tans.thprofparser.writeUnsignedInt
import okio.BufferedSink

internal class LoadClassRecorderWriter(
    tag: Int,
    timeStamp: Long,
    header: HprofHeader,
    private val sink: BufferedSink
) : LoadClassRecorderVisitor(
    tag = tag,
    timeStamp = timeStamp,
    header = header
) {
    private var classSerialNumber: Long? = null
    private var id: Long? = null
    private var stackTraceSerialNumber: Long? = null
    private var classNameStringId: Long? = null

    override fun visitLoadClass(
        classSerialNumber: Long,
        id: Long,
        stackTraceSerialNumber: Long,
        classNameStringId: Long
    ) {
        this.classSerialNumber = classSerialNumber
        this.id = id
        this.stackTraceSerialNumber = stackTraceSerialNumber
        this.classNameStringId = classNameStringId
    }

    override fun visitEnd() {
        val classSerialNumber = this.classSerialNumber
        val id = this.id
        val stackTraceSerialNumber = this.stackTraceSerialNumber
        val classNameStringId = this.classNameStringId
        if (classSerialNumber == null || id == null || stackTraceSerialNumber == null || classNameStringId == null) {
            throw HprofParserException("Do not invoke visitLoadClass() method.")
        }
        sink.writeUnsignedByte(tag)
        sink.writeUnsignedInt(timeStamp)
        sink.writeInt(4 + header.identifierByteSize + 4 + header.identifierByteSize)
        sink.writeUnsignedInt(classSerialNumber)
        sink.writeId(id, header)
        sink.writeUnsignedInt(stackTraceSerialNumber)
        sink.writeId(classNameStringId, header)
    }
}