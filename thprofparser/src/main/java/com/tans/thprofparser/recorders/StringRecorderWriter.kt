package com.tans.thprofparser.recorders

import com.tans.thprofparser.HprofHeader
import com.tans.thprofparser.HprofParserException
import com.tans.thprofparser.writeId
import com.tans.thprofparser.writeUnsignedByte
import com.tans.thprofparser.writeUnsignedInt
import okio.BufferedSink

internal class StringRecorderWriter(
    tag: Int,
    timeStamp: Long,
    header: HprofHeader,
    private val sink: BufferedSink
) : StringRecorderVisitor(
    tag = tag,
    timeStamp = timeStamp,
    header = header
) {
    private var id: Long? = null

    private var str: String? = null

    override fun visitString(id: Long, str: String) {
        this.id = id
        this.str = str
    }

    override fun visitEnd() {
        val id: Long? = this.id
        val str: String? = this.str
        if (id == null || str == null) {
            throw HprofParserException("Do not invoke visitString() method.")
        }
        sink.writeUnsignedByte(tag)
        sink.writeUnsignedInt(timeStamp)
        val bodyBytes = str.toByteArray(Charsets.UTF_8)
        sink.writeUnsignedInt((bodyBytes.size + header.identifierByteSize).toLong())
        sink.writeId(id, header)
        sink.write(bodyBytes)
    }

}