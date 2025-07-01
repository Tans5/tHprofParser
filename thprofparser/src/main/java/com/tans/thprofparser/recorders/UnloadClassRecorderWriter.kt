package com.tans.thprofparser.recorders

import com.tans.thprofparser.HprofHeader
import com.tans.thprofparser.HprofParserException
import com.tans.thprofparser.writeUnsignedByte
import com.tans.thprofparser.writeUnsignedInt
import okio.BufferedSink

internal class UnloadClassRecorderWriter(
    tag: Int,
    timeStamp: Long,
    header: HprofHeader,
    private val sink: BufferedSink
) : UnloadClassRecorderVisitor(tag, timeStamp, header) {

    private var classSerialNumber: Long? = null

    override fun visitUnloadClassRecorder(classSerialNumber: Long) {
        this.classSerialNumber = classSerialNumber
    }

    override fun visitEnd() {
        val classSerialNumber = this.classSerialNumber
        if (classSerialNumber == null) {
            throw HprofParserException("Do not invoke visitUnloadClassRecorder() method.")
        }
        sink.writeUnsignedByte(tag)
        sink.writeUnsignedInt(timeStamp)
        sink.writeUnsignedInt(4)
        sink.writeUnsignedInt(classSerialNumber)
    }
}