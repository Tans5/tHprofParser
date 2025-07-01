package com.tans.thprofparser.recorders

import com.tans.thprofparser.HprofHeader
import com.tans.thprofparser.HprofParserException
import com.tans.thprofparser.writeId
import com.tans.thprofparser.writeUnsignedByte
import com.tans.thprofparser.writeUnsignedInt
import okio.BufferedSink

internal class StackTraceRecorderWriter(
    tag: Int,
    timeStamp: Long,
    header: HprofHeader,
    private val sink: BufferedSink
) : StackTraceRecorderVisitor(tag, timeStamp, header) {

    private var stackTraceSerialNumber: Long? = null
    private var threadSerialNumber: Long? = null
    private var stackFrameIds: List<Long>? = null

    override fun visitStackTrace(
        stackTraceSerialNumber: Long,
        threadSerialNumber: Long,
        stackFrameIds: List<Long>
    ) {
        this.stackTraceSerialNumber = stackTraceSerialNumber
        this.threadSerialNumber = threadSerialNumber
        this.stackFrameIds = stackFrameIds
    }

    override fun visitEnd() {
        val stackTraceSerialNumber = this.stackTraceSerialNumber
        val threadSerialNumber = this.threadSerialNumber
        val stackFrameIds = this.stackFrameIds
        if (stackTraceSerialNumber == null || threadSerialNumber == null || stackFrameIds == null) {
            throw HprofParserException("Do not invoke visitStackTrace() method.")
        }
        sink.writeUnsignedByte(tag)
        sink.writeUnsignedInt(timeStamp)
        sink.writeUnsignedInt((stackFrameIds.size * header.identifierByteSize + 4 * 2).toLong())
        sink.writeUnsignedInt(stackTraceSerialNumber)
        sink.writeUnsignedInt(threadSerialNumber)
        for (id in stackFrameIds) {
            sink.writeId(id, header)
        }
    }
}