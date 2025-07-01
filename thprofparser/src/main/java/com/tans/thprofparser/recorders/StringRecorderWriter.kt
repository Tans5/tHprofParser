package com.tans.thprofparser.recorders

import com.tans.thprofparser.recorders.StringRecorderVisitor.Companion.StringContext
import com.tans.thprofparser.writeId
import com.tans.thprofparser.writeUnsignedByte
import com.tans.thprofparser.writeUnsignedInt
import okio.BufferedSink

internal class StringRecorderWriter(private val sink: BufferedSink) : StringRecorderVisitor() {

    private var stringContext: StringContext? = null

    override fun visitString(stringContext: StringContext) {
        this.stringContext = stringContext
    }

    override fun visitEnd() {
        val stringContext = this.stringContext
        if (stringContext == null) {
            return
        }
        val recorderContext = stringContext.recorderContext
        sink.writeUnsignedByte(recorderContext.tag)
        sink.writeUnsignedInt(recorderContext.timestamp)
        val bodyBytes = stringContext.str.toByteArray(Charsets.UTF_8)
        sink.writeUnsignedInt((bodyBytes.size + recorderContext.header.identifierByteSize).toLong())
        sink.writeId(stringContext.id, recorderContext.header)
        sink.write(bodyBytes)
    }

}