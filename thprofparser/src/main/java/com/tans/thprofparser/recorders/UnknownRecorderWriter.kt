package com.tans.thprofparser.recorders

import com.tans.thprofparser.recorders.UnknownRecorderVisitor.Companion.UnknownBodyContext
import com.tans.thprofparser.writeUnsignedByte
import com.tans.thprofparser.writeUnsignedInt
import okio.BufferedSink

internal class UnknownRecorderWriter(
    private val sink: BufferedSink
) : UnknownRecorderVisitor() {

    private var unknownBodyContext: UnknownBodyContext? = null

    override fun visitBody(unknownBodyContext: UnknownBodyContext) {
        this.unknownBodyContext = unknownBodyContext
    }

    override fun visitEnd() {
        val unknownBodyContext = this.unknownBodyContext
        if (unknownBodyContext == null) {
            return
        }
        val recorderContext = unknownBodyContext.recorderContext
        sink.writeUnsignedByte(recorderContext.tag)
        sink.writeUnsignedInt(recorderContext.timestamp)
        sink.writeUnsignedInt(unknownBodyContext.body.size.toLong())
        sink.write(unknownBodyContext.body)
        sink.flush()
    }
}