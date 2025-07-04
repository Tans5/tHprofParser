package com.tans.thprofparser

import com.tans.thprofparser.records.HeapDumpRecordVisitor
import com.tans.thprofparser.records.Record
import com.tans.thprofparser.records.RecordContext

open class HprofVisitor(protected val nextVisitor: HprofVisitor? = null) {

    open fun visitHeader(header: HprofHeader) {
        nextVisitor?.visitHeader(header)
    }

    open fun visitStringRecord(context: RecordContext<Record.StringRecord>) {
        nextVisitor?.visitStringRecord(context)
    }

    open fun visitLoadClassRecord(context: RecordContext<Record.LoadClassRecord>) {
        nextVisitor?.visitLoadClassRecord(context)
    }

    open fun visitUnloadClassRecord(context: RecordContext<Record.UnloadClassRecord>) {
        nextVisitor?.visitUnloadClassRecord(context)
    }

    open fun visitStackFrameRecord(context: RecordContext<Record.StackFrameRecord>) {
        nextVisitor?.visitStackFrameRecord(context)
    }

    open fun visitStackTraceRecord(context: RecordContext<Record.StackTraceRecord>) {
        nextVisitor?.visitStackTraceRecord(context)
    }

    open fun visitHeapDumpRecord(
        tag: Int,
        timestamp: Long,
        header: HprofHeader
    ): HeapDumpRecordVisitor? {
        return nextVisitor?.visitHeapDumpRecord(tag, timestamp, header)
    }

    open fun visitUnknownRecord(
        context: RecordContext<Record.UnknownRecord>
    ) {
        nextVisitor?.visitUnknownRecord(context)
    }

    open fun visitEnd() {
        nextVisitor?.visitEnd()
    }
}