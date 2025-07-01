package com.tans.thprofparser.recorders

import com.tans.thprofparser.HprofHeader
import com.tans.thprofparser.HprofParserException
import com.tans.thprofparser.writeUnsignedByte
import com.tans.thprofparser.writeUnsignedInt
import okio.BufferedSink

internal class UnknownRecorderWriter(
    tag: Int,
    timeStamp: Long,
    header: HprofHeader,
    private val sink: BufferedSink
) : UnknownRecorderVisitor(tag, timeStamp, header) {

    private var body: ByteArray? = null

    override fun visitBody(body: ByteArray) {
        this.body = body
    }

    override fun visitEnd() {
        val body: ByteArray? = this.body
        if (body == null) {
            throw HprofParserException("Do not invoke visitBody() method.")
        }
        sink.writeUnsignedByte(tag)
        sink.writeUnsignedInt(timeStamp)
        sink.writeUnsignedInt(body.size.toLong())
        sink.write(body)
    }
}